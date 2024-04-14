plugins {
  id(afterroot.plugins.android.library.get().pluginId)
  id(afterroot.plugins.kotlin.android.get().pluginId)
  id(afterroot.plugins.android.hilt.get().pluginId)
  id(afterroot.plugins.watchdone.android.common.get().pluginId)
}

android {
  namespace = "com.afterroot.watchdone.data.search"
}

dependencies {
  implementation(projects.core.logging)
  implementation(projects.data)
  implementation(projects.data.databaseRoom)
}
