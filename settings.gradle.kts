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

@file:Suppress("UnstableApiUsage")

import java.util.Properties

pluginManagement {
  includeBuild("gradle/build-logic")

  repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
  }
}

dependencyResolutionManagement {
  val properties = readProperties(file("private.properties"))

  repositories {
    google()
    mavenCentral()

    maven {
      name = "github-afterroot-utils"
      url = uri("https://maven.pkg.github.com/afterroot/utils")
      credentials {
        username = properties.getProperty("gpr.user") ?: System.getenv("GHUSERNAME")
        password = properties.getProperty("gpr.key") ?: System.getenv("GHTOKEN")
      }
    }
  }

  versionCatalogs {
    create("afterroot") {
      from(files("gradle/build-logic/convention.versions.toml"))
    }
  }
}

plugins {
  id("com.gradle.develocity") version "3.17.5"
  id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

develocity {
  buildScan {
    termsOfUseUrl = "https://gradle.com/terms-of-service"
    termsOfUseAgree = "yes"
  }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "watchdone"

include(
  ":app",
  ":api:tmdb",
  ":ards",
  ":base",
  ":common:ui:compose",
  ":common:ui:resources",
  ":core:logging",
  ":core:testing",
  ":data",
  ":data:database",
  ":data:database-room",
  ":data:datastore",
  ":data:datastore-proto",
  ":data:discover",
  ":data:media",
  ":data:model",
  ":data:search",
  ":domain",
  ":themoviedbapi",
  ":test-app",
  ":ui:discover",
  ":ui:media",
  ":ui:profile",
  ":ui:recommended",
  ":ui:search",
  ":ui:settings",
  ":ui:watchlist",
  // ":utils",
  ":tmdb-api",
)

project(":ards").projectDir = file("ards/lib") // AfterROOT Data Structure
// project(":utils").projectDir = file("utils/lib") // AfterROOT Utils
project(":tmdb-api").projectDir = file("tmdb-kotlin/tmdb-api")

fun readProperties(propertiesFile: File): Properties {
  if (!propertiesFile.exists()) {
    return Properties()
  }
  return Properties().apply {
    propertiesFile.inputStream().use { fis -> load(fis) }
  }
}
