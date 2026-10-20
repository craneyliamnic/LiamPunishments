plugins {
    id("liampunishments.platform-conventions")
    alias(libs.plugins.run.velocity)
}

dependencies {
    implementation(project(":liampunishments-core"))
    compileOnly(libs.velocity.api)
}

tasks {
    processResources {
        val properties = mapOf("version" to project.version.toString())
        inputs.properties(properties)
        filesMatching("velocity-plugin.json") {
            expand(properties)
        }
    }

    runVelocity {
        velocityVersion(libs.versions.velocity.get())
    }
}
