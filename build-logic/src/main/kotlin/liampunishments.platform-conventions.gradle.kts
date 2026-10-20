// Applied to modules that produce a deployable plugin jar (Paper, Velocity).
plugins {
    id("liampunishments.java-conventions")
    id("com.gradleup.shadow")
}

tasks.shadowJar {
    archiveBaseName = "LiamPunishments-${project.name.removePrefix("liampunishments-").replaceFirstChar(Char::titlecase)}"
    archiveClassifier = ""
    mergeServiceFiles()
}

tasks.jar {
    archiveClassifier = "plain"
}

tasks.assemble {
    dependsOn(tasks.shadowJar)
}
