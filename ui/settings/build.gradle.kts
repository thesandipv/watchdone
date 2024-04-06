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
  alias(libs.plugins.jetbrains.kotlin.kapt)
}

android {
  namespace = "com.afterroot.watchdone.ui.settings"
  buildFeatures.buildConfig = true
}

dependencies {
  implementation(projects.data)
  implementation(projects.data.databaseRoom)

  implementation(libs.androidx.appCompat)
  implementation(libs.androidx.core)
  implementation(libs.androidx.fragment)
  implementation(libs.androidx.lifecycle.viewmodel)
  implementation(libs.androidx.navigation.fragment)
  implementation(libs.androidx.preference)
  implementation(libs.androidx.transition)
  implementation(libs.bundles.lifecycle)

  implementation(libs.hilt.compose)

  implementation(libs.google.ossLic)
  implementation(libs.google.material)

  implementation(platform(libs.firebase.bom))
  implementation(libs.firebase.firestore)
}
