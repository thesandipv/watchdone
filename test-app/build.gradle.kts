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

plugins {
  id(afterroot.plugins.android.test.get().pluginId)
  id(afterroot.plugins.kotlin.android.get().pluginId)
  id(afterroot.plugins.watchdone.android.common.get().pluginId)
  alias(libs.plugins.jetbrains.kotlin.kapt)
}

android {
  namespace = "com.afterroot.watchdone.test"
  targetProjectPath = ":app"

  defaultConfig {
    testInstrumentationRunner = "com.afterroot.watchdone.core.testing.WatchdoneTestRunner"
  }
}

dependencies {
  implementation(projects.app)
  implementation(projects.core.testing)
  implementation(projects.data)

  implementation(libs.androidx.test.core)
  implementation(libs.androidx.test.runner)
  implementation(libs.hilt.testing)
  implementation(libs.kotlinx.coroutines.test)
  implementation(libs.test.junit)
  kapt(libs.hilt.compiler)
}
