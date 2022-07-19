enableFeaturePreview("VERSION_CATALOGS")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
enableFeaturePreview("STABLE_CONFIGURATION_CACHE")

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
    "services:database-service",
    "configuration",
    "http:http-shared",
    "http:http-server",
    "http:http-test",
    "http:http-routes"
)