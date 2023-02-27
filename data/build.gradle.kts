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

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.ksp)
}

apply(from = "$rootDir/gradle/common-config.gradle.kts")
apply(from = "$rootDir/gradle/common-config-library.gradle")
apply(from = "$rootDir/gradle/oss-licence.gradle")

android {
    namespace = "com.afterroot.watchdone.data"
    defaultConfig {
        testInstrumentationRunner = "com.afterroot.watchdone.data.test.DataTestRunner"

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
    }
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
    arg("room.incremental", "true")
}

dependencies {
    api(projects.ards)
    api(projects.themoviedbapi)
    implementation(projects.base)
    implementation(projects.ui.resources)

    implementation(libs.androidx.preference)
    implementation(libs.androidx.paging)
    api(libs.androidx.datastore)

    api(libs.google.gson)

    api(libs.store)

    implementation(libs.okhttp.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.retrofit.retrofit)
    implementation(libs.retrofit.jackson)

    implementation(libs.bundles.firebase)

    implementation(libs.bundles.coroutines)

    api(libs.androidx.room.room)
    api(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)

    implementation(libs.hilt.hilt)
    kapt(libs.hilt.compiler)

    testImplementation(libs.androidx.room.test)
    testImplementation(libs.androidx.test.archCore)
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.hilt.testing)
    testImplementation(libs.kotlin.coroutines.test)
    testImplementation(libs.test.mockk)
    testImplementation(libs.test.junit)
    testImplementation(libs.androidx.test.junitExt)
    testImplementation(libs.test.robolectric)

    kaptTest(libs.hilt.compiler)
    kaptTest(libs.androidx.room.compiler)

    androidTestImplementation(libs.androidx.test.core)
    androidTestImplementation(libs.androidx.test.junitExt)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.hilt.testing)
    androidTestImplementation(libs.kotlin.coroutines.test)
    androidTestImplementation(libs.test.junit)

    kaptAndroidTest(libs.hilt.compiler)

    implementation(libs.coil)
}
