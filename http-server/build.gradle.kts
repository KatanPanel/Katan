subprojects {
    dependencies {
        compileOnly(rootProject.libs.ktor.server.host.common)
        implementation(rootProject.libs.ktor.server.feature.resources)
        testImplementation(rootProject.libs.ktor.server.test)
    }
}