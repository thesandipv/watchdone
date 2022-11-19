plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
}

apply(from = "$rootDir/gradle/common-config.gradle.kts")
apply(from = "$rootDir/gradle/compose.gradle")
apply(from = "$rootDir/gradle/oss-licence.gradle")

android {
    namespace = "com.afterroot.watchdone.recommended"
    buildFeatures {
        dataBinding = true
    }
}

dependencies {
    implementation(projects.base)
    implementation(projects.data)
    implementation(projects.domain)
    implementation(projects.ui.common)
    implementation(projects.ui.commonCompose)
    implementation(projects.ui.resources)

    implementation(libs.androidx.fragment)
    implementation(libs.androidx.paging)
    implementation(libs.bundles.lifecycle)

    implementation(libs.google.material)

    implementation(libs.hilt.hilt)
    implementation(libs.hilt.compose)
    kapt(libs.hilt.compiler)
}
