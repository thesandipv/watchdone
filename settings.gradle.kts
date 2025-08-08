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
  repositories {
    google()
    mavenCentral()
  }

  versionCatalogs {
    create("afterroot") {
      from(files("gradle/build-logic/convention.versions.toml"))
    }
  }
}

plugins {
  id("com.gradle.develocity") version "3.19.2"
  id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
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
  ":data:tmdb-auth",
  ":data:tmdb-account",
  ":domain",
  ":test-app",
  ":ui:discover",
  ":ui:media",
  ":ui:profile",
  ":ui:recommended",
  ":ui:search",
  ":ui:settings",
  ":ui:watchlist",
  ":utils",
  ":tmdb-api",
)

project(":ards").projectDir = file("ards/lib") // AfterROOT Data Structure
project(":utils").projectDir = file("utils/lib") // AfterROOT Utils
project(":tmdb-api").projectDir = file("tmdb-kotlin/tmdb-api")

fun readProperties(propertiesFile: File): Properties {
  if (!propertiesFile.exists()) {
    return Properties()
  }
  return Properties().apply {
    propertiesFile.inputStream().use { fis -> load(fis) }
  }
}
