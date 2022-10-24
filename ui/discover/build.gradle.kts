plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.android)
    id("androidx.navigation.safeargs")
    id("kotlin-kapt")
}

apply(from = "$rootDir/gradle/common-config.gradle.kts")
apply(from = "$rootDir/gradle/oss-licence.gradle")

android {
    namespace = "com.afterroot.watchdone.ui.discover"
    buildFeatures {
        dataBinding = true
        viewBinding = true
    }
}

dependencies {
    //All compose dependencies applied with compose.gradle
    implementation(projects.base)
    implementation(projects.data)
    implementation(projects.domain)
    implementation(projects.ui.common)
    implementation(projects.ui.commonCompose)
    implementation(projects.ui.media)
    implementation(projects.ui.resources)

    implementation(libs.androidx.constraintLayout)
    implementation(libs.androidx.core)
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.recyclerView)
    implementation(libs.androidx.transition)
    implementation(libs.bundles.lifecycle)

    implementation(libs.hilt.hilt)
    implementation(libs.hilt.compose)
    kapt(libs.hilt.compiler)

    implementation(libs.materialProgress)
}
