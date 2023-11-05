plugins {
    id(afterroot.plugins.android.library.get().pluginId)
    id(afterroot.plugins.kotlin.android.get().pluginId)
    id(afterroot.plugins.android.hilt.get().pluginId)
    id(afterroot.plugins.watchdone.android.common.get().pluginId)
}

android {
    namespace = "com.afterroot.watchdone.data.media"
}

dependencies {
    implementation(projects.api.tmdb)
    implementation(projects.core.logging)
    implementation(projects.data)
    implementation(projects.data.database)
    implementation(projects.data.model)

    testImplementation(projects.core.testing)
}
