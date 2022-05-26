enableFeaturePreview("VERSION_CATALOGS")

rootProject.name = "katan-server"

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

include(
    "grpc:stub",
    "grpc:protos",
    "model",
    "application",
    "core",
    "runtime",
    "services:service-container",
    "services:service-server",
    "http-server:http-server-shared",
    "http-server:http-server-core",
    "http-server:http-server-test"
)