/*
 * Copyright (C) 2020-2023 Sandip Vaghela
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.afterroot.watchdone.ui.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.afterroot.tmdbapi.model.config.Country
import com.afterroot.tmdbapi.repository.ConfigRepository
import com.afterroot.watchdone.base.Constants
import com.afterroot.watchdone.database.dao.CountriesDao
import com.afterroot.watchdone.settings.Settings
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.jakewharton.processphoenix.ProcessPhoenix
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
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

    @Inject
    @Named("version_string")
    lateinit var versionString: String

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
            AppCompatDelegate.setDefaultNightMode(
                when (newValue) {
                    getString(
                        CommonR.string.theme_device_default,
                    ),
                    -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                    getString(
                        CommonR.string.theme_battery,
                    ),
                    -> AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
                    getString(CommonR.string.theme_light) -> AppCompatDelegate.MODE_NIGHT_NO
                    getString(CommonR.string.theme_dark) -> AppCompatDelegate.MODE_NIGHT_YES
                    else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                },
            )
            return@setOnPreferenceChangeListener true
        }

        findPreference<PreferenceCategory>("key_debug")?.isVisible = BuildConfig.DEBUG

        findPreference<SwitchPreferenceCompat>(
            "http_logging",
        )?.setOnPreferenceChangeListener { _, _ ->
            ProcessPhoenix.triggerRebirth(requireContext())
            true
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
                    countriesDao.add(configRepository.getCountries())
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
}
