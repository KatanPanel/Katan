plugins {
    application
    id("com.github.johnrengelman.shadow") version "5.0.0"
}
dependencies {
    implementation("io.ktor:ktor-jackson:1.4.0")
    implementation(project(":api"))
    implementation(project(":core"))
}

application {
    mainClassName = "io.ktor.server.netty.EngineMain"
}

tasks.withType<Jar> {
    manifest {
        attributes(
            mapOf(
                "Main-Class" to application.mainClassName
            )
        )
    }
}