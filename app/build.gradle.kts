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

import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.util.Properties

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.gms.googleServices)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    id("androidx.navigation.safeargs")
    id("com.google.android.gms.oss-licenses-plugin")
    id("kotlin-parcelize")
}

kapt {
    correctErrorTypes = true
    useBuildCache = true
}

apply(from = "$rootDir/gradle/oss-licence.gradle")
apply(from = "$rootDir/gradle/apply-core.gradle")

hilt {
    enableAggregatingTask = true
}

val ci by extra { System.getenv("CI") == "true" }

android {
    buildFeatures {
        viewBinding = true
        dataBinding = true
        compose = true
    }

    defaultConfig {
        namespace = "com.afterroot.watchdone"
        applicationId = "com.afterroot.watchdone"
        versionCode = rootProject.extra["versionCode"] as Int
        versionName = rootProject.extra["versionName"].toString()
        vectorDrawables {
            useSupportLibrary = true
        }
        multiDexEnabled = true

        testInstrumentationRunner = "com.afterroot.watchdone.di.HiltTestRunner"

        manifestPlaceholders += mapOf(
            "hostName" to "afterroot.web.app",
            "pathPrefix" to "/apps/watchdone/launch"
        )

        resourceConfigurations.addAll(listOf("en"))

        val tmdbPropertiesFile = rootProject.file("tmdb.properties")
        val tmdbProperties = Properties()
        if (tmdbPropertiesFile.exists()) {
            tmdbProperties.load(FileInputStream(tmdbPropertiesFile))
        }
        buildConfigField(
            "String",
            "TMDB_BEARER_TOKEN",
            tmdbProperties["tmdbBearerToken"] as String? ?: System.getenv("TMDB_BEARER_TOKEN")
        )
        buildConfigField("String", "TMDB_API", tmdbProperties["tmdbApi"] as String? ?: System.getenv("TMDB_API"))
        buildConfigField("String", "FB_APP_ID", tmdbProperties["fbAppId"] as String? ?: System.getenv("FB_APP_ID"))

        val commitHash = ByteArrayOutputStream()
        exec {
            commandLine("git", "rev-parse", "--short", "HEAD")
            standardOutput = commitHash
        }

        val commit = System.getenv("COMMIT_ID") ?: commitHash.toString().trim()
        buildConfigField("String", "COMMIT_ID", "\"$commit\"")
    }

    val keystorePropertiesFile = rootProject.file("keystore.properties")
    val keystoreProperties = Properties()
    if (keystorePropertiesFile.exists()) {
        keystoreProperties.load(FileInputStream(keystorePropertiesFile))
    }

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
            // multiDexKeepProguard(file("proguard-rules.pro"))

            signingConfig = signingConfigs["release"]
        }
        debug {
            extra["alwaysUpdateBuildId"] = false
            // splits.abi.enable = false
            // splits.density.enable = false
            // crunchPngs = false

            applicationIdSuffix = ".debug"
            versionNameSuffix = "-beta"

            signingConfig = signingConfigs["release"]
        }
    }

    packagingOptions {
        packagingOptions.resources.excludes += setOf(
            "META-INF/proguard/*",
            "/*.properties",
            "fabric/*.properties",
            "META-INF/*.properties",
            "META-INF/LICENSE*.md"
        )
    }

    lint {
        abortOnError = false
    }

    composeOptions {
        println("- INFO: Compose BOM Version: ${libs.versions.composeBom.get()}")
        println("- INFO: Compose Compiler Version: ${libs.versions.composeCompiler.get()}")
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }
}

dependencies {

    implementation(projects.base)
    implementation(projects.data)
    implementation(projects.ui.common)
    implementation(projects.ui.commonCompose)
    implementation(projects.ui.discover)
    implementation(projects.ui.media)
    implementation(projects.ui.recommended)
    implementation(projects.ui.resources)
    implementation(projects.ui.search)
    implementation(projects.ui.settings)
    implementation(projects.ui.watchlist)

    implementation(libs.kotlin.stdLib)

    implementation(libs.androidx.appCompat)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.billing)
    implementation(libs.androidx.cardView)
    implementation(libs.androidx.constraintLayout)
    implementation(libs.androidx.core)
    implementation(libs.androidx.fragment)
    implementation(libs.bundles.lifecycle)
    implementation(libs.androidx.multiDex)
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)
    implementation(libs.androidx.paging)
    implementation(libs.androidx.palette)
    implementation(libs.androidx.preference)
    implementation(libs.androidx.recyclerView)
    implementation(libs.androidx.startUp)
    implementation(libs.androidx.supportV13)
    implementation(libs.androidx.supportV4)
    implementation(libs.androidx.vectorDrawable)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose)
    debugImplementation(libs.androidx.compose.tooling)

    implementation(libs.materialdialogs.input)
    implementation(libs.materialdialogs.core)
    implementation(libs.materialdialogs.bottomSheets)
    implementation(libs.materialdialogs.color)

    implementation(libs.firebase.ui.auth)
    implementation(libs.firebase.ui.firestore)
    implementation(libs.firebase.ui.storage)

    implementation(libs.hilt.hilt)
    implementation(libs.hilt.compose)
    kapt(libs.hilt.compiler)

    kapt(libs.androidx.room.compiler)

    implementation(libs.google.ossLic)
    implementation(libs.google.material)
    implementation(libs.bundles.firebase)

    implementation(libs.commonsIo)

    implementation(libs.kotlin.coroutines.android)
    testImplementation(libs.kotlin.coroutines.test)

    implementation(libs.materialProgress)

    implementation(libs.okhttp.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.retrofit.retrofit)
    implementation(libs.retrofit.jackson)

    testImplementation(libs.test.junit)
    androidTestImplementation(libs.androidx.test.junitExt)
    androidTestImplementation(libs.androidx.test.espresso)

    testImplementation(libs.androidx.test.core)
    testImplementation("org.mockito:mockito-core:5.2.0")
    androidTestImplementation("org.mockito:mockito-android:5.2.0")

    implementation("androidx.work:work-runtime-ktx:2.8.0")
    implementation(libs.google.auth)

    androidTestImplementation(libs.androidx.test.archCore)
    androidTestImplementation(libs.androidx.test.core)
    androidTestImplementation(libs.hilt.testing)
    androidTestImplementation(libs.kotlin.coroutines.test)
    androidTestImplementation(libs.test.mockk)
    androidTestImplementation(libs.test.robolectric)
}
