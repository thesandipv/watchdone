plugins {
  id(afterroot.plugins.android.library.get().pluginId)
  id(afterroot.plugins.android.hilt.get().pluginId)
  id(afterroot.plugins.android.compose.get().pluginId)
  id(afterroot.plugins.watchdone.android.common.get().pluginId)
}

android {
  namespace = "com.afterroot.watchdone.ui.settings"
  buildFeatures.buildConfig = true
}

dependencies {
  implementation(projects.data)
  implementation(projects.data.databaseRoom)

  implementation(libs.androidx.appCompat)
  implementation(libs.androidx.core)
  implementation(libs.androidx.fragment)
  implementation(libs.androidx.lifecycle.viewmodel)
  implementation(libs.androidx.navigation.fragment)
  implementation(libs.androidx.preference)
  implementation(libs.androidx.transition)
  implementation(libs.bundles.lifecycle)

  implementation(libs.hilt.compose)

  implementation(libs.google.material)

  implementation(platform(libs.firebase.bom))
  implementation(libs.firebase.firestore)
}
