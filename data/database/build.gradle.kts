plugins {
    id("com.afterroot.android.library")
    id("com.afterroot.kotlin.android")
    id("com.afterroot.watchdone.android.common")

    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.afterroot.watchdone.database"

    defaultConfig {
        testInstrumentationRunner = "com.afterroot.watchdone.data.test.DataTestRunner"
    }
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
    arg("room.incremental", "true")
}

dependencies {
    api(projects.data.model)

    implementation(libs.androidx.room.room)
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)

    implementation(libs.hilt.hilt)
    kapt(libs.hilt.compiler)

    implementation(libs.bundles.coroutines)
}
