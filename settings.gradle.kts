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
    "services:service-id",
    "services:service-account",
    "services:service-container",
    "services:service-server",
    "http:http-shared",
    "http:http-server",
    "http:http-test",
    "http:http-routes-server"
)