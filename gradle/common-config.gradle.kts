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
    println("- INFO: Compose Enabled for ${project.path}")
    apply(from = "$rootDir/gradle/compose.gradle")
}

apply(from = "$rootDir/gradle/apply-common-deps.gradle")
apply(from = "$rootDir/gradle/apply-core.gradle")
