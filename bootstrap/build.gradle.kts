plugins {
    application
    id("com.github.johnrengelman.shadow") version "5.0.0"
}

repositories {
    maven("https://dl.bintray.com/marshallpierce/maven")
}

val ktorVersion = "1.4.0"
dependencies {
    implementation(project(":core"))
    implementation("io.ktor:ktor-locations:$ktorVersion")
    implementation("io.ktor:ktor-auth-jwt:$ktorVersion")
    implementation("io.ktor:ktor-server-cio:$ktorVersion")
    implementation("org.mpierce.ktor.csrf:ktor-csrf:0.5.0")
}

application {
    mainClassName = "io.ktor.server.netty.EngineMain"
}

tasks {
    shadowJar {
        archiveBaseName.set("Katan-${project.parent?.version ?: project.version}")
    }

    jar {
        manifest {
            attributes["Main-Class"] = application.mainClassName
        }
    }

    build {
        dependsOn(shadowJar)
    }
}