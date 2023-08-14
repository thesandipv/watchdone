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

import com.afterroot.gradle.readProperties

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("com.afterroot.android.library")
    id("com.afterroot.kotlin.android")
    alias(libs.plugins.kotlin.kapt)
}

android {
    namespace = "com.afterroot.watchdone.base"

    buildFeatures.buildConfig = true

    defaultConfig {
        val commitHash = providers.exec {
            commandLine("git", "rev-parse", "--short", "HEAD")
        }.standardOutput.asText.get()

        val commit = System.getenv("COMMIT_ID") ?: commitHash.trim()
        buildConfigField("String", "COMMIT_ID", "\"$commit\"")
        buildConfigField("int", "VERSION_CODE", "${rootProject.extra["versionCode"]}")
        buildConfigField("String", "VERSION_NAME", "\"${rootProject.extra["versionName"]}\"")

        val tmdbProperties = readProperties(rootProject.file("tmdb.properties"))
        buildConfigField(
            "String",
            "TMDB_BEARER_TOKEN",
            tmdbProperties["tmdbBearerToken"] as String? ?: System.getenv("TMDB_BEARER_TOKEN"),
        )
        buildConfigField("String", "TMDB_API", tmdbProperties["tmdbApi"] as String? ?: System.getenv("TMDB_API"))
        buildConfigField("String", "FB_APP_ID", tmdbProperties["fbAppId"] as String? ?: System.getenv("FB_APP_ID"))
    }
}

dependencies {
    api(libs.kotlin.coroutines.core)

    implementation(libs.androidx.recyclerView)
    api(libs.androidx.lifecycle.common)
    api(libs.androidx.lifecycle.extensions)

    api(libs.glide.glide)

    implementation(libs.commonsCodec)

    api(libs.timber)

    implementation(libs.hilt.hilt)
    kapt(libs.hilt.compiler)
}
