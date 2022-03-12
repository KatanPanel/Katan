enableFeaturePreview("VERSION_CATALOGS")

rootProject.name = "katan-server"

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

include("katan-api")
include("katan-core")
include("katan-common")
include("katan-plugin-api")
include("katan-core-docker")