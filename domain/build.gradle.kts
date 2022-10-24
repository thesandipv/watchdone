plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
}

apply(from = "$rootDir/gradle/common-config.gradle.kts")
apply(from = "$rootDir/gradle/common-config-library.gradle")
apply(from = "$rootDir/gradle/oss-licence.gradle")

android {
    namespace = "com.afterroot.watchdone.domain"
}

dependencies {
    implementation(projects.base)
    implementation(projects.data)

    implementation(libs.androidx.core)
    implementation(libs.androidx.lifecycle.viewmodel)

    implementation(libs.hilt.hilt)
    implementation(libs.hilt.compose)
    kapt(libs.hilt.compiler)
}
