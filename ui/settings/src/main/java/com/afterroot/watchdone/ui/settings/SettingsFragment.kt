/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */
package com.afterroot.watchdone.ui.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import app.moviebase.tmdb.model.TmdbConfigurationCountry
import com.afterroot.watchdone.base.Constants
import com.afterroot.watchdone.data.mapper.toCountry
import com.afterroot.watchdone.data.model.Country
import com.afterroot.watchdone.data.model.DarkThemeConfig
import com.afterroot.watchdone.data.model.UserData
import com.afterroot.watchdone.data.repositories.ConfigRepository
import com.afterroot.watchdone.database.dao.CountriesDao
import com.afterroot.watchdone.di.VersionFormatted
import com.afterroot.watchdone.settings.Settings
import com.afterroot.watchdone.utils.State
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.FirebaseFirestore
import com.jakewharton.processphoenix.ProcessPhoenix
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import timber.log.Timber
import com.afterroot.watchdone.resources.R as CommonR

@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat() {
  private var countriesJob: Job? = null

  @Inject lateinit var settings: Settings

  @Inject lateinit var configRepository: ConfigRepository

  @Inject lateinit var countriesDao: CountriesDao

  @Inject lateinit var firestore: FirebaseFirestore

  @Inject
  @VersionFormatted
  lateinit var versionString: String

  private val settingsActivityViewModel: SettingsActivityViewModel by activityViewModels()

  private var uiState: State<UserData> by mutableStateOf(State.loading())

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // Update the uiState
    lifecycleScope.launch {
      lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
        settingsActivityViewModel.uiState
          .onEach { uiState = it }
          .collect()
      }
    }
  }

  override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
    setPreferencesFromResource(CommonR.xml.pref_settings, rootKey)

    findPreference<ListPreference>(Constants.PREF_KEY_IMAGE_SIZE)?.apply {
      with(settings.posterSizes?.toTypedArray()) {
        entries = this
        entryValues = this
      }
    }

    findPreference<ListPreference>(
      Constants.PREF_KEY_THEME,
    )?.setOnPreferenceChangeListener { _, newValue ->
      // TODO Remove AppCompat
      AppCompatDelegate.setDefaultNightMode(
        when (newValue) {
          getString(
            CommonR.string.theme_device_default,
          ),
          -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM

          getString(CommonR.string.theme_light) -> AppCompatDelegate.MODE_NIGHT_NO
          getString(CommonR.string.theme_dark) -> AppCompatDelegate.MODE_NIGHT_YES
          else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        },
      )

      settingsActivityViewModel.updateSettings {
        val dtc = when (newValue) {
          getString(
            CommonR.string.theme_device_default,
          ),
          -> DarkThemeConfig.FOLLOW_SYSTEM

          getString(CommonR.string.theme_light) -> DarkThemeConfig.LIGHT
          getString(CommonR.string.theme_dark) -> DarkThemeConfig.DARK
          else -> DarkThemeConfig.FOLLOW_SYSTEM
        }
        setDarkThemeConfig(dtc)
      }

      return@setOnPreferenceChangeListener true
    }

    findPreference<Preference>("oss_lic")?.setOnPreferenceClickListener {
      OssLicensesMenuActivity.setActivityTitle(
        getString(com.google.android.gms.oss.licenses.R.string.oss_license_title),
      )
      requireContext().startActivity<OssLicensesMenuActivity>()
      return@setOnPreferenceClickListener true
    }

    findPreference<Preference>(getString(CommonR.string.key_version))?.summary = versionString

    updateCountryPref()
    setUpDebugPreferences()
  }

  private fun updateCountryPref() {
    val preference = findPreference<Preference>("key_countries")
    lifecycleScope.launch {
      settings.country?.let {
        val dbCountry = countriesDao.get(it).flowOn(Dispatchers.IO)

        dbCountry.collect { country ->
          preference?.summary = country?.englishName
        }
      }
    }
    preference?.setOnPreferenceClickListener {
      showCountrySelectDialog()
      return@setOnPreferenceClickListener true
    }
  }

  private fun showCountrySelectDialog() {
    countriesJob?.cancel()
    countriesJob = lifecycleScope.launch {
      withContext(Dispatchers.IO) {
        if (countriesDao.count() == 0) {
          withContext(Dispatchers.Main) {
            requireContext().toast("Please Wait...")
          }
          countriesDao.add(
            configRepository.getCountries().map(TmdbConfigurationCountry::toCountry),
          )
        }
      }

      var dialog: MaterialAlertDialogBuilder?

      countriesDao.getCountriesFlow().flowOn(Dispatchers.IO).map {
        it.map(Country::englishName).toTypedArray()
      }.collectLatest { countryArray ->
        dialog = MaterialAlertDialogBuilder(requireContext()).setTitle("Select Country")
          .setSingleChoiceItems(countryArray, 0) { dialog, which ->
            Timber.d("showCountrySelectDialog: ${countryArray[which]}")
            lifecycleScope.launch {
              countriesDao.getByName(countryArray[which]).flowOn(Dispatchers.IO).collectLatest {
                settings.country = it?.iso
                findPreference<Preference>("key_countries")?.summary = it?.englishName
                dialog.dismiss()
              }
            }
          }
        dialog?.show()
      }
    }
  }

  private fun setUpDebugPreferences() {
    if (!BuildConfig.DEBUG) return
    findPreference<PreferenceCategory>("key_debug")?.isVisible = true

    findPreference<SwitchPreferenceCompat>(
      "http_logging",
    )?.setOnPreferenceChangeListener { _, _ ->
      ProcessPhoenix.triggerRebirth(requireContext())
      true
    }

    findPreference<SwitchPreferenceCompat>(
      "key_enable_emulator",
    )?.setOnPreferenceChangeListener { _, _ ->
      ProcessPhoenix.triggerRebirth(requireContext())
      true
    }

    findPreference<SwitchPreferenceCompat>(
      "use_prod_db",
    )?.setOnPreferenceChangeListener { _, _ ->
      ProcessPhoenix.triggerRebirth(requireContext())
      true
    }

    findPreference<Preference>("key_clear_persistence")?.apply {
      onPreferenceClickListener = Preference.OnPreferenceClickListener {
        firestore.terminate().also {
          if (it.isSuccessful) {
            firestore.clearPersistence()
            ProcessPhoenix.triggerRebirth(requireContext())
          }
        }
        true
      }
    }
  }
}
