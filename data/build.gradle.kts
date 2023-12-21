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
    id(afterroot.plugins.android.library.get().pluginId)
    id(afterroot.plugins.kotlin.android.get().pluginId)
    id(afterroot.plugins.android.compose.get().pluginId)
    id(afterroot.plugins.android.hilt.get().pluginId)
    id(afterroot.plugins.watchdone.android.common.get().pluginId)

    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.afterroot.watchdone.data"

    buildFeatures.buildConfig = true

    defaultConfig {
        testInstrumentationRunner = "com.afterroot.watchdone.core.testing.WatchdoneTestRunner"

        val tmdbProperties = readProperties(rootProject.file("tmdb.properties"))
        buildConfigField(
            "String",
            "TMDB_BEARER_TOKEN",
            tmdbProperties["tmdbBearerToken"] as String? ?: System.getenv("TMDB_BEARER_TOKEN"),
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
    api(projects.data.model)
    api(projects.themoviedbapi)
    implementation(projects.api.tmdb)
    implementation(projects.core.logging)

    implementation(libs.androidx.preference)
    implementation(libs.androidx.paging)
    api(libs.androidx.datastore)

    api(libs.google.gson)

    api(libs.store)

    api(libs.okhttp.okhttp)
    api(libs.okhttp.logging)
    api(libs.retrofit.retrofit)
    api(libs.retrofit.jackson)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.config)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.crashlytics)

    implementation(libs.bundles.coroutines)

    api(libs.androidx.room.room)
    api(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)

    testImplementation(libs.androidx.room.test)
    testImplementation(libs.androidx.test.archCore)
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.hilt.testing)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.test.mockk)
    testImplementation(libs.test.junit)
    testImplementation(libs.androidx.test.junitExt)
    testImplementation(libs.test.robolectric)

    kaptTest(libs.androidx.room.compiler)

    androidTestImplementation(libs.androidx.test.core)
    androidTestImplementation(libs.androidx.test.junitExt)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.hilt.testing)
    androidTestImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.test.junit)

    implementation(libs.coil)

    implementation(libs.kotlinx.datetime)
}
