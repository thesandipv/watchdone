plugins {
    id(afterroot.plugins.android.library.get().pluginId)
    id(afterroot.plugins.kotlin.android.get().pluginId)
    id(afterroot.plugins.watchdone.android.common.get().pluginId)

    alias(libs.plugins.google.ksp)
}

android {
    namespace = "com.afterroot.watchdone.database"

    defaultConfig {
        testInstrumentationRunner = "com.afterroot.watchdone.core.testing.WatchdoneTestRunner"
    }
}

dependencies {
    api(projects.data.model)
    api(libs.androidx.paging.common)
}
