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

import com.afterroot.gradle.readProperties

plugins {
    id(afterroot.plugins.android.application.get().pluginId)
    id(afterroot.plugins.kotlin.android.get().pluginId)
    id(afterroot.plugins.android.compose.get().pluginId)
    id(afterroot.plugins.android.hilt.get().pluginId)
    id(afterroot.plugins.watchdone.android.common.get().pluginId)

    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.google.gms)
    alias(libs.plugins.google.ksp)

    id(libs.plugins.google.ossLic.get().pluginId)
}

val ci by extra { System.getenv("CI") == "true" }

android {
    namespace = "com.afterroot.watchdone"

    buildFeatures.buildConfig = true

    defaultConfig {
        applicationId = "com.afterroot.watchdone"
        versionCode = rootProject.extra["versionCode"] as Int
        versionName = rootProject.extra["versionName"].toString()

        testInstrumentationRunner = "com.afterroot.watchdone.core.testing.WatchdoneTestRunner"

        manifestPlaceholders += mapOf("hostName" to "afterroot.web.app", "pathPrefix" to "/apps/watchdone/launch")

        resourceConfigurations.addAll(listOf("en"))
    }

    val keystoreProperties = readProperties(rootProject.file("keystore.properties"))

    signingConfigs {
        create("release") {
            storeFile = rootProject.file("release/keystore.jks")
            storePassword = keystoreProperties["storePassword"] as String? ?: System.getenv("SIGN_STORE_PW")
            keyAlias = "watchdone"
            keyPassword = keystoreProperties["keyPassword"] as String? ?: System.getenv("SIGN_KEY_PW")
        }
    }

    buildTypes {
        release {
            isShrinkResources = true
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs["release"]
        }
        debug {
            extra["alwaysUpdateBuildId"] = false
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-beta"
            signingConfig = signingConfigs["release"]
            isMinifyEnabled = false
        }
    }

    lint.abortOnError = false

    packaging.resources.excludes +=
        setOf(
            "META-INF/proguard/*",
            "/*.properties",
            "fabric/*.properties",
            "META-INF/*.properties",
            "META-INF/LICENSE*.md",
            "META-INF/**/previous-compilation-data.bin",
        )
}

dependencies {
    implementation(projects.api.tmdb)
    implementation(projects.core.logging)
    implementation(projects.data)
    implementation(projects.domain)
    implementation(projects.ui.discover)
    implementation(projects.ui.media)
    implementation(projects.ui.profile)
    implementation(projects.ui.recommended)
    implementation(projects.ui.search)
    implementation(projects.ui.settings)
    implementation(projects.ui.watchlist)

    implementation(libs.androidx.activity)
    implementation(libs.androidx.core)
    implementation(libs.androidx.core.splash)
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.palette)
    implementation(libs.bundles.lifecycle)

    implementation(libs.coil)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.ads)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.ui.auth)

    implementation(libs.google.ossLic)

    implementation(libs.hilt.compose)

    implementation(libs.okhttp.logging)
    implementation(libs.okhttp.okhttp)

    implementation(libs.retrofit.jackson)
    implementation(libs.retrofit.retrofit)
}
