// Declared once here so all subprojects share one plugin classloader (Spotless build service fails otherwise).
plugins {
    id("liampunishments.platform-conventions") apply false
    alias(libs.plugins.run.paper) apply false
    alias(libs.plugins.run.velocity) apply false
}
