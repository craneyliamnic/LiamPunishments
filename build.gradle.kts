// Declares every plugin once on the root classpath so all subprojects share the same plugin classes.
// Module configuration lives in build-logic convention plugins and each module's own build script.
plugins {
    id("liampunishments.platform-conventions") apply false
    alias(libs.plugins.run.paper) apply false
    alias(libs.plugins.run.velocity) apply false
}
