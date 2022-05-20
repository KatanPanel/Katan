subprojects {
    apply(plugin = rootProject.libs.plugins.serialization.get().pluginId)

    dependencies {
        compileOnly(rootProject.libs.ktor.server.host.common)
        implementation(rootProject.libs.ktor.server.feature.resources)
        implementation(rootProject.libs.koin.ktor)
    }
}