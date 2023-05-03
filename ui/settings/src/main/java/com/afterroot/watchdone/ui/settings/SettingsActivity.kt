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

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.databinding.DataBindingUtil
import com.afterroot.ui.common.compose.components.CommonAppBar
import com.afterroot.ui.common.compose.theme.Theme
import com.afterroot.watchdone.settings.Settings
import com.afterroot.watchdone.ui.settings.databinding.FragmentSettingsBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {

    @Inject lateinit var settings: Settings
    lateinit var binding: FragmentSettingsBinding

    @SuppressLint("CommitTransaction")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.fragment_settings)

        binding.fragmentSettingsAppBarCompose.apply {
            setContent {
                Theme(context = this@SettingsActivity, settings = settings) {
                    CommonAppBar(withTitle = "Settings")
                }
            }
        }

        val fragment = SettingsFragment()
        supportFragmentManager.beginTransaction().replace(binding.fragmentSettingsContainer.id, fragment).commit()
    }
}