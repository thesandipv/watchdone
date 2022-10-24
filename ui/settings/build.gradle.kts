plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
}

apply(from = "$rootDir/gradle/common-config.gradle.kts")
apply(from = "$rootDir/gradle/common-config-library.gradle")
apply(from = "$rootDir/gradle/oss-licence.gradle")

android {
    namespace = "com.afterroot.watchdone.ui.settings"
    buildFeatures {
        dataBinding = true
        viewBinding = true
    }
}

dependencies {
    //All compose dependencies applied with compose.gradle
    implementation(projects.base)
    implementation(projects.data)
    implementation(projects.ui.common)
    implementation(projects.ui.commonCompose)
    implementation(projects.ui.resources)

    implementation(libs.androidx.core)
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.transition)
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.bundles.lifecycle)

    implementation(libs.hilt.hilt)
    implementation(libs.hilt.compose)
    kapt(libs.hilt.compiler)
}
