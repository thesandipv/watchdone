plugins {
    id(afterroot.plugins.android.library.get().pluginId)
    id(afterroot.plugins.kotlin.android.get().pluginId)
    id(afterroot.plugins.android.hilt.get().pluginId)
    id(afterroot.plugins.watchdone.android.common.get().pluginId)

    alias(libs.plugins.ksp)
}

android {
    namespace = "com.afterroot.watchdone.database"

    defaultConfig {
        testInstrumentationRunner = "com.afterroot.watchdone.core.testing.WatchdoneTestRunner"
    }
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
    arg("room.incremental", "true")
}

dependencies {
    implementation(projects.data)
    api(projects.data.model)
    api(projects.data.database)

    implementation(libs.androidx.room.room)
    implementation(libs.androidx.room.paging)
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)

    implementation(libs.bundles.coroutines)

    implementation(libs.kotlinx.datetime)

    implementation(libs.androidx.paging.common)

    androidTestImplementation(projects.core.testing)
}
