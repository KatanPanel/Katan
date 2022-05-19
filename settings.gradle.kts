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
include("services:service-container", "services:service-server")
include("http-server:http-server-shared", "http-server:http-server-core", "http-server:module-server")