plugins {
    id("liampunishments.platform-conventions")
    alias(libs.plugins.run.paper)
}

dependencies {
    implementation(project(":liampunishments-core"))
    compileOnly(libs.paper.api)
}

tasks {
    processResources {
        val properties = mapOf(
            "version" to project.version.toString(),
            "apiVersion" to libs.versions.minecraft.get(),
        )
        inputs.properties(properties)
        filesMatching("paper-plugin.yml") {
            expand(properties)
        }
    }

    runServer {
        minecraftVersion(libs.versions.minecraft.get())
        jvmArgs("-Xms2G", "-Xmx2G")
    }
}
