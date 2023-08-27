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
    id("com.afterroot.android.library")
    id("com.afterroot.kotlin.android")
    id("com.afterroot.watchdone.android.common")
    id("com.afterroot.android.compose")
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.kapt)
}

android {
    namespace = "com.afterroot.watchdone.recommended"
    buildFeatures {
        dataBinding = true
    }
}

dependencies {
    implementation(projects.data)
    implementation(projects.domain)
    implementation(projects.common)
    implementation(projects.common.ui.compose)

    implementation(libs.androidx.fragment)
    implementation(libs.androidx.paging)
    implementation(libs.bundles.lifecycle)

    implementation(libs.google.material)

    implementation(libs.hilt.hilt)
    implementation(libs.hilt.compose)
    kapt(libs.hilt.compiler)
}
