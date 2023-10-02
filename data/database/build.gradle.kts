plugins {
    id(afterroot.plugins.android.library.get().pluginId)
    id(afterroot.plugins.kotlin.android.get().pluginId)
    id(afterroot.plugins.watchdone.android.common.get().pluginId)

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
