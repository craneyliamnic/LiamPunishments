plugins {
    `java-library`
    id("com.diffplug.spotless")
}

val libs = versionCatalogs.named("libs")

java {
    toolchain.languageVersion = JavaLanguageVersion.of(25)
}

dependencies {
    compileOnlyApi(libs.findLibrary("jspecify").get())

    testImplementation(platform(libs.findLibrary("junit-bom").get()))
    testImplementation(libs.findLibrary("junit-jupiter").get())
    testImplementation(libs.findLibrary("assertj").get())
    testRuntimeOnly(libs.findLibrary("junit-platform-launcher").get())
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = Charsets.UTF_8.name()
    options.release = 25
    options.compilerArgs.addAll(listOf("-Xlint:all", "-Xlint:-processing", "-Werror", "-parameters"))
}

tasks.withType<Javadoc>().configureEach {
    options.encoding = Charsets.UTF_8.name()
}

tasks.test {
    useJUnitPlatform()
}

spotless {
    java {
        palantirJavaFormat()
        removeUnusedImports()
        trimTrailingWhitespace()
        endWithNewline()
        licenseHeaderFile(rootProject.file("gradle/license-header.txt"))
    }
}
