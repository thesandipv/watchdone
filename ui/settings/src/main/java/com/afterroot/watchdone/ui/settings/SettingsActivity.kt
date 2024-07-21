/*
 * Copyright (C) 2020-2024 Sandip Vaghela
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
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.afterroot.ui.common.compose.components.CommonAppBar
import com.afterroot.ui.common.compose.theme.Theme
import com.afterroot.ui.common.compose.utils.shouldUseDarkTheme
import com.afterroot.watchdone.data.model.UserData
import com.afterroot.watchdone.settings.Settings
import com.afterroot.watchdone.utils.State
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsActivity : FragmentActivity() {

  @Inject lateinit var settings: Settings

  private val settingsActivityViewModel: SettingsActivityViewModel by viewModels()

  @SuppressLint("CommitTransaction")
  @OptIn(ExperimentalMaterial3Api::class)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    var uiState: State<UserData> by mutableStateOf(State.loading())

    // Update the uiState
    lifecycleScope.launch {
      lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
        settingsActivityViewModel.uiState
          .onEach { uiState = it }
          .collect()
      }
    }

    enableEdgeToEdge()
    setContentView(R.layout.fragment_settings)

    findViewById<ComposeView>(R.id.fragment_settings_app_bar_compose).apply {
      setContent {
        Theme(settings = settings, darkTheme = shouldUseDarkTheme(uiState)) {
          CommonAppBar(withTitle = "Settings")
        }
      }
    }

    val fragment = SettingsFragment()
    supportFragmentManager.beginTransaction().replace(
      R.id.fragment_settings_container,
      fragment,
    ).commit()
  }
}
