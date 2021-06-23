/*
 * Copyright (C) 2020-2021 Sandip Vaghela
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
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.afterroot.watchdone.BuildConfig
import com.afterroot.watchdone.R
import com.afterroot.watchdone.base.Constants
import com.afterroot.watchdone.settings.Settings
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import org.jetbrains.anko.startActivity
import org.koin.android.ext.android.inject

class SettingsFragment : PreferenceFragmentCompat() {
    private val settings: Settings by inject()
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.pref_settings, rootKey)
        findPreference<ListPreference>(Constants.PREF_KEY_IMAGE_SIZE)?.apply {
            with(settings.posterSizes?.toTypedArray()) {
                entries = this
                entryValues = this
            }
        }
        findPreference<ListPreference>(Constants.PREF_KEY_THEME)?.setOnPreferenceChangeListener { _, newValue ->
            AppCompatDelegate.setDefaultNightMode(
                when (newValue) {
                    getString(R.string.theme_device_default) -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                    getString(R.string.theme_battery) -> AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
                    getString(R.string.theme_light) -> AppCompatDelegate.MODE_NIGHT_NO
                    getString(R.string.theme_dark) -> AppCompatDelegate.MODE_NIGHT_YES
                    else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                }
            )
            return@setOnPreferenceChangeListener true
        }

        findPreference<Preference>("oss_lic")?.setOnPreferenceClickListener {
            OssLicensesMenuActivity.setActivityTitle(getString(R.string.oss_license_title))
            requireContext().startActivity<OssLicensesMenuActivity>()
            return@setOnPreferenceClickListener true
        }

        findPreference<Preference>(getString(R.string.key_version))?.summary = "v${BuildConfig.VERSION_NAME}"
    }
}
