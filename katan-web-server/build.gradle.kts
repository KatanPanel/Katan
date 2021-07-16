val ktorVersion = "1.4.0"

repositories {
    maven(Repositories.Modules.WS.javaJwt)
}

dependencies {
    implementation(project(":katan-core"))
    implementation(project(":katan-database"))
    implementation(project(":katan-database-jdbc"))
    implementation(Dependencies.Modules.WS.javaJwt)
    implementation(Dependencies.Modules.WS.jacksonDataBind)
    implementation(Dependencies.Modules.WS.Ktor.serverNetty)
    implementation(Dependencies.Modules.WS.Ktor.serverCore)
    implementation(Dependencies.Modules.WS.Ktor.serverTestHost)
    implementation(Dependencies.Modules.WS.Ktor.networkTls)
    implementation(Dependencies.Modules.WS.Ktor.authJwt)
    implementation(Dependencies.Modules.WS.Ktor.locations)
    implementation(Dependencies.Modules.WS.Ktor.websockets)
    implementation(Dependencies.Modules.WS.Ktor.jackson)
    implementation(Dependencies.Koin.ktor)
}