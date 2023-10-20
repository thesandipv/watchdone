plugins {
    id(afterroot.plugins.android.library.get().pluginId)
    id(afterroot.plugins.kotlin.android.get().pluginId)
    id(afterroot.plugins.watchdone.android.common.get().pluginId)
}

android {
    namespace = "com.afterroot.watchdone.data.model"
}

dependencies {
    api(projects.themoviedbapi)
    implementation(projects.ards)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.core)
    implementation(libs.firebase.firestore)
    implementation(libs.kotlinx.datetime)
    api(libs.tmdb.api)
}
