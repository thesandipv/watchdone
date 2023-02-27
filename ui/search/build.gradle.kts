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

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.hilt)
}

apply(from = "$rootDir/gradle/common-config.gradle.kts")
apply(from = "$rootDir/gradle/oss-licence.gradle")

android {
    namespace = "com.afterroot.watchdone.search"
}

dependencies {
    // All compose dependencies applied with compose.gradle
    implementation(projects.base)
    implementation(projects.data)
    implementation(projects.domain)
    implementation(projects.ui.common)
    implementation(projects.ui.commonCompose)
    implementation(projects.ui.media)
    implementation(projects.ui.resources)

    implementation(libs.androidx.fragment)
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.navigation.fragment)

    implementation(libs.hilt.hilt)
    implementation(libs.hilt.compose)
    kapt(libs.hilt.compiler)
}
