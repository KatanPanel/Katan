rootProject.name = "katan-server"
include("api")
include("core")
include("common")
include("web-server")
include("cli")
include("file-system")

pluginManagement {
    repositories {
        gradlePluginPortal()
        jcenter()
    }
}