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


pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

plugins {
    id("com.gradle.enterprise") version "3.13.3"
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.4.0"
}

gradleEnterprise {
    buildScan {
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"
        publishAlways()
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
// enableFeaturePreview("STABLE_CONFIGURATION_CACHE")

rootProject.name = "watchdone"

include(
    ":app",
    ":base",
    ":data",
    ":ards",
    ":domain",
    ":themoviedbapi",
    ":ui:common",
    ":ui:common-compose",
    ":ui:discover",
    ":ui:media",
    ":ui:profile",
    ":ui:resources",
    ":ui:recommended",
    ":ui:search",
    ":ui:settings",
    ":ui:watchlist",
    ":utils"
)

project(":utils").projectDir = file("utils/lib") // AfterROOT Utils
project(":ards").projectDir = file("ards/lib") // AfterROOT Data Structure
