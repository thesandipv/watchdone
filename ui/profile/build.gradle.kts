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

plugins {
  id(afterroot.plugins.android.library.get().pluginId)
  id(afterroot.plugins.kotlin.android.get().pluginId)
  id(afterroot.plugins.android.hilt.get().pluginId)
  id(afterroot.plugins.android.compose.get().pluginId)
  id(afterroot.plugins.watchdone.android.common.get().pluginId)
}

android {
  namespace = "com.afterroot.watchdone.ui.profile"
}

dependencies {
  implementation(projects.data)
  implementation(projects.domain)
  implementation(projects.data.tmdbAuth)
  implementation(projects.data.tmdbAccount)

  implementation(libs.androidx.core)
  implementation(libs.androidx.lifecycle.viewmodel)
  implementation(libs.androidx.transition)

  implementation(libs.hilt.compose)

  implementation(libs.firebase.ui.auth)

  implementation(libs.bundles.coroutines)

  implementation(platform(libs.firebase.bom))
  implementation(libs.firebase.firestore)
}
