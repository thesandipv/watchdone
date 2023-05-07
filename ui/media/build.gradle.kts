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

import java.io.FileInputStream
import java.util.Properties

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
}

apply(from = "$rootDir/gradle/common-config.gradle.kts")
apply(from = "$rootDir/gradle/compose.gradle")
apply(from = "$rootDir/gradle/oss-licence.gradle")

android {
    namespace = "com.afterroot.watchdone.media"
    defaultConfig {
        val tmdbPropertiesFile = rootProject.file("tmdb.properties")
        val tmdbProperties = Properties()
        if (tmdbPropertiesFile.exists()) {
            tmdbProperties.load(FileInputStream(tmdbPropertiesFile))
        }
        buildConfigField("String", "FB_APP_ID", tmdbProperties["fbAppId"] as String? ?: System.getenv("FB_APP_ID"))
    }
}

dependencies {
    implementation(projects.base)
    implementation(projects.data)
    implementation(projects.domain)
    implementation(projects.ui.common)
    implementation(projects.ui.commonCompose)
    implementation(projects.ui.resources)
    implementation(projects.ui.recommended)

    implementation(libs.androidx.appCompat)
    implementation(libs.androidx.constraintLayout)
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.paging)
    implementation(libs.androidx.palette)
    implementation(libs.androidx.recyclerView)
    implementation(libs.androidx.supportV13)
    implementation(libs.bundles.lifecycle)

    implementation(libs.google.material)
    implementation(libs.google.ads)

    implementation(libs.firebase.firestore)

    implementation(libs.hilt.hilt)
    implementation(libs.hilt.compose)
    kapt(libs.hilt.compiler)

    implementation(libs.materialProgress)
}
