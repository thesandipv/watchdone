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
    alias(libs.plugins.gms.googleServices)
    alias(libs.plugins.ksp)

    id("com.google.android.gms.oss-licenses-plugin")
}

val ci by extra { System.getenv("CI") == "true" }

android {
    namespace = "com.afterroot.watchdone"

    buildFeatures {
        viewBinding = true
        dataBinding = true
        buildConfig = true
    }

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
    implementation(projects.data)
    implementation(projects.domain)
    implementation(projects.ui.discover)
    implementation(projects.ui.media)
    implementation(projects.ui.profile)
    implementation(projects.ui.recommended)
    implementation(projects.ui.search)
    implementation(projects.ui.settings)
    implementation(projects.ui.watchlist)

    // implementation(libs.kotlin.stdLib)

    implementation(libs.androidx.appCompat)
    implementation(libs.androidx.activity)
    // implementation(libs.androidx.billing)
    // implementation(libs.androidx.cardView)
    implementation(libs.androidx.constraintLayout)
    implementation(libs.androidx.core)
    implementation(libs.androidx.fragment)
    implementation(libs.bundles.lifecycle)
    // implementation(libs.androidx.multiDex)
    // implementation(libs.androidx.navigation.fragment)
    // implementation(libs.androidx.navigation.ui)
    // implementation(libs.androidx.paging)
    implementation(libs.androidx.palette)
    // implementation(libs.androidx.preference)
    // implementation(libs.androidx.recyclerView)
    // implementation(libs.androidx.startUp)
    // implementation(libs.androidx.supportV13)
    // implementation(libs.androidx.supportV4)
    // implementation(libs.androidx.vectorDrawable)

    // implementation(platform(libs.androidx.compose.bom))
    // implementation(libs.bundles.compose)
    // debugImplementation(libs.androidx.compose.tooling)

    implementation(libs.coil)

    implementation(libs.firebase.ui.auth)
    // implementation(libs.firebase.ui.firestore)
    // implementation(libs.firebase.ui.storage)

    implementation(libs.hilt.compose)

    // ksp(libs.androidx.room.compiler)

    // implementation(libs.google.ossLic)
    // implementation(libs.google.material)

    implementation(platform(libs.firebase.bom))
    // implementation(libs.bundles.firebase)
    implementation(libs.firebase.ads)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.messaging)

    // implementation(libs.commonsIo)

    // implementation(libs.kotlin.coroutines.android)
    // testImplementation(libs.kotlin.coroutines.test)

    // implementation(libs.materialProgress)

    implementation(libs.okhttp.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.retrofit.retrofit)
    implementation(libs.retrofit.jackson)

    // testImplementation(libs.test.junit)
    // androidTestImplementation(libs.androidx.test.junitExt)
    // androidTestImplementation(libs.androidx.test.espresso)

    testImplementation(libs.androidx.test.core)
    testImplementation("org.mockito:mockito-core:5.8.0")
    androidTestImplementation("org.mockito:mockito-android:5.8.0")

    // implementation(libs.androidx.work)
    // implementation(libs.google.auth)

    // androidTestImplementation(libs.androidx.test.archCore)
    // androidTestImplementation(libs.androidx.test.core)
    // androidTestImplementation(libs.hilt.testing)
    // androidTestImplementation(libs.kotlin.coroutines.test)
    // androidTestImplementation(libs.test.mockk)
    // androidTestImplementation(libs.test.robolectric)
}
