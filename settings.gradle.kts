enableFeaturePreview("VERSION_CATALOGS")

rootProject.name = "katan-server"

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

include("grpc:stub", "grpc:protos")
include("maestro")
include("data-types")
include("application")
include("core")
include("runtime")