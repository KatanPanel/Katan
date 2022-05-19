enableFeaturePreview("VERSION_CATALOGS")

rootProject.name = "katan-server"

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

include("grpc:stub", "grpc:protos")
include("model")
include("application")
include("core")
include("runtime")
include("services:container-service")
include("http-server:routing", "http-server:server")