//check project.path is in list of projects
val composeProjects =
    arrayOf(
        ":data",
        ":ui:common-compose",
        ":ui:discover",
        ":ui:media",
        ":ui:recommended",
        ":ui:search",
        ":ui:settings",
        ":ui:watchlist",
    )
if (project.path in composeProjects) {
    println("- INFO: Compose Enabled")
    apply(from = "$rootDir/gradle/compose.gradle")
}

val implementation by configurations

dependencies {
    implementation(libs.kotlin.stdLib)
    implementation(libs.androidx.core)
}

apply(from = "$rootDir/gradle/apply-core.gradle")
