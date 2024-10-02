plugins {
  id(afterroot.plugins.android.library.get().pluginId)
  id(afterroot.plugins.kotlin.android.get().pluginId)
  id(afterroot.plugins.android.hilt.get().pluginId)
  id(afterroot.plugins.watchdone.android.common.get().pluginId)
}

android {
  namespace = "com.afterroot.watchdone.tmdb"
}

dependencies {
  api(libs.okhttp.okhttp)
  api(libs.okhttp.doh)
  api(projects.tmdbApi)
  implementation(projects.core.logging)
  implementation(projects.data.tmdbAuth)

  implementation(libs.ktor.core)
  implementation(libs.ktor.okhttp)
}
