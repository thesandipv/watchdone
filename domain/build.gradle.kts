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
    id(afterroot.plugins.watchdone.android.common.get().pluginId)
}

android {
    namespace = "com.afterroot.watchdone.domain"
    buildFeatures.dataBinding = true
}

dependencies {
    implementation(projects.data)

    implementation(libs.androidx.core)
    implementation(libs.androidx.lifecycle.viewmodel)

    implementation(libs.hilt.compose)

    api(libs.androidx.paging.common)
    implementation(libs.androidx.paging)

    implementation(platform(libs.firebase.bom))
    implementation(libs.bundles.firebase)
}
