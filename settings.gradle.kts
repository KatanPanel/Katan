enableFeaturePreview("VERSION_CATALOGS")

rootProject.name = "katan-server"

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

include("protocol:stub", "protocol:protos")
include("control")
include("model")
include("snowflake-id")