rootProject.name = "katan-server"

pluginManagement {
    repositories {
        gradlePluginPortal()
        jcenter()
    }
}

include("api")
include("core")
include("common")
include("web-server")
include("cli")
include("file-system")
include("bootstrap")