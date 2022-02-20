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
include("katan-web-server")
include("katan-bootstrap")
include("katan-database-jdbc")
include("katan-database")
include("katan-plugin-api")
include("katan-core-docker")
include("katan-database-api")
include("katan-database-mongodb")
