plugins {
  id(afterroot.plugins.android.library.get().pluginId)
  id(afterroot.plugins.kotlin.android.get().pluginId)
  alias(libs.plugins.google.protobuf)
}

android {
  namespace = "com.afterroot.watchdone.datastore.proto"
}

// Setup protobuf configuration, generating lite Java and Kotlin classes
protobuf {
  protoc {
    artifact = libs.protobuf.protoc.get().toString()
  }
  generateProtoTasks {
    all().forEach { task ->
      task.builtins {
        register("java") {
          option("lite")
        }
        register("kotlin") {
          option("lite")
        }
      }
    }
  }
}

androidComponents.beforeVariants {
  android.sourceSets.maybeCreate(it.name).apply {
    val buildDir = layout.buildDirectory.get().asFile
    java.srcDir(buildDir.resolve("generated/source/proto/${it.name}/java"))
    kotlin.srcDir(buildDir.resolve("generated/source/proto/${it.name}/kotlin"))
  }
}

dependencies {
  api(libs.protobuf.kotlin.lite)
}
