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

// Top-level build file where you can add configuration options common to all sub-projects/modules.

import com.android.build.gradle.BaseExtension
import com.android.build.gradle.BasePlugin
import dagger.hilt.android.plugin.HiltExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath(libs.google.pluginOssLic)
        classpath(libs.androidx.navigation.pluginSafeArgs)
    }
}

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.android.lint) apply false
    alias(libs.plugins.android.test) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
    alias(libs.plugins.gms.googleServices) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.kapt) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.spotless)
}

val isVersionPropertiesExists = rootProject.file("version.properties").exists()
val versionProperties = java.util.Properties()
if (isVersionPropertiesExists) {
    versionProperties.load(java.io.FileInputStream(rootProject.file("version.properties")))
}

val major = libs.versions.major.get().toInt()
val minor = libs.versions.minor.get().toInt()
val patch = versionProperties["patch"].toString().toInt()
val versionCode: Int by extra { libs.versions.minSdk.get().toInt() * 10000000 + major * 10000 + minor * 100 + patch }
val versionName: String by extra { "${major}.${minor}.${patch}" }

println("-INFO: Build version code: $versionCode")

allprojects {
    val isPublishPropertiesExists = rootProject.file("publish.properties").exists()
    val properties = java.util.Properties()
    if (isPublishPropertiesExists) {
        properties.load(java.io.FileInputStream(rootProject.file("publish.properties")))
    }
    repositories {
        google()
        mavenCentral()

        // Jetpack Compose SNAPSHOTs if needed
        // maven("https://androidx.dev/snapshots/builds/$composeSnapshot/artifacts/repository/")

        // Used for snapshots if needed
        // maven("https://oss.sonatype.org/content/repositories/snapshots/")

        maven {
            name = "github-afterroot-utils"
            url = uri("https://maven.pkg.github.com/afterroot/utils")
            credentials {
                username = properties.getProperty("gpr.user") ?: System.getenv("GHUSERNAME")
                password = properties.getProperty("gpr.key") ?: System.getenv("GHTOKEN")
            }
        }
    }

    // Configure Java to use our chosen language level. Kotlin will automatically
    // pick this up
    plugins.withType<JavaBasePlugin>().configureEach {
        extensions.configure<JavaPluginExtension> {
            toolchain {
                languageVersion.set(JavaLanguageVersion.of(11))
            }
        }
    }
}

subprojects {
    apply(plugin = rootProject.libs.plugins.spotless.get().pluginId)
    spotless {
        format("misc") {
            // define the files to apply `misc` to
            target("*.gradle", "*.md", ".gitignore")
            indentWithSpaces()
            // define the steps to apply to those files
            trimTrailingWhitespace()
            endWithNewline()
        }
        kotlin {
            target("**/*.kt")
            targetExclude("$buildDir/**/*.kt")
            targetExclude("bin/**/*.kt")

            ktlint(libs.versions.ktlint.get())
                .editorConfigOverride(
                    mapOf(
                        "ktlint_disabled_rules" to "enum-entry-name-case,annotation",
                        "disabled_rules" to "enum-entry-name-case,annotation"
                    )
                )
        }
        kotlinGradle {
            target("**/*.kts")
            targetExclude("$buildDir/**/*.kts")
            ktlint(libs.versions.ktlint.get())
        }
    }

    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions {
            // Treat all Kotlin warnings as errors
            allWarningsAsErrors = false

            // Enable experimental coroutines APIs, including Flow
            freeCompilerArgs = freeCompilerArgs + listOf(
                "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
                "-opt-in=kotlinx.coroutines.FlowPreview",
                "-opt-in=kotlin.Experimental"
            )

            if (project.hasProperty("enableComposeCompilerReports")) {
                freeCompilerArgs = freeCompilerArgs + listOf(
                    "-P",
                    "plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=" +
                        project.buildDir.absolutePath + "/compose_metrics"
                )
                freeCompilerArgs = freeCompilerArgs + listOf(
                    "-P",
                    "plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=" +
                        project.buildDir.absolutePath + "/compose_metrics"
                )
            }

            // Set JVM target to 11
            jvmTarget = JavaVersion.VERSION_11.toString()
        }
    }

    plugins.withId(rootProject.libs.plugins.hilt.get().pluginId) {
        extensions.getByType<HiltExtension>().enableAggregatingTask = true
    }
    plugins.withId(rootProject.libs.plugins.kotlin.kapt.get().pluginId) {
        extensions.getByType<org.jetbrains.kotlin.gradle.plugin.KaptExtension>().apply {
            correctErrorTypes = true
            useBuildCache = true
        }
    }
    plugins.withType<BasePlugin>().configureEach {
        extensions.configure<BaseExtension> {
            compileSdkVersion(libs.versions.compileSdk.get().toInt())
            defaultConfig {
                minSdk = libs.versions.minSdk.get().toInt()
                targetSdk = libs.versions.targetSdk.get().toInt()
            }
            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_11
                targetCompatibility = JavaVersion.VERSION_11
            }
        }
    }
}

apply(from = "$rootDir/gradle/dependencyGraph.gradle")

task("incrementPatch") {
    doLast {
        versionProperties["patch"] = (patch + 1).toString()
        versionProperties.store(rootProject.file("version.properties").writer(), null)
        println("-INFO: Patch changed from $patch to ${versionProperties["patch"]}")
    }
}
