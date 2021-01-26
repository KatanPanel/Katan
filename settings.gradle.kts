rootProject.name = "katan-server"

pluginManagement {
    repositories {
        gradlePluginPortal()
        jcenter()
    }
}

include("katan-api")
include("katan-core")
include("katan-common")
include("katan-web-server")
include("katan-cli")
include("katan-io")
include("katan-bootstrap")