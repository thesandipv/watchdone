plugins {
    id(afterroot.plugins.android.library.get().pluginId)
    id(afterroot.plugins.kotlin.android.get().pluginId)
    id(afterroot.plugins.watchdone.android.common.get().pluginId)
}

android {
    namespace = "com.afterroot.watchdone.data.model"
}

dependencies {
    api(libs.tmdb.api)
    api(projects.themoviedbapi)
    implementation(libs.androidx.room.common)
    implementation(libs.firebase.core)
    implementation(libs.firebase.firestore)
    implementation(libs.kotlinx.datetime)
    implementation(platform(libs.firebase.bom))
    implementation(projects.ards)
}
