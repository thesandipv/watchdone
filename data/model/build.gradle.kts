plugins {
    id("com.afterroot.android.library")
    id("com.afterroot.kotlin.android")
    id("com.afterroot.watchdone.android.common")
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
}
