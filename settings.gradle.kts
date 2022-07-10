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
    "services:id-service",
    "services:unit-service",
    "services:unit-instance-service",
    "services:docker-unit-instance-service",
    "services:account-service",
    "http:http-shared",
    "http:http-server",
    "http:http-test",
    "http:http-routes"
)