plugins {
    application
    id("com.github.johnrengelman.shadow") version "5.0.0"
}

dependencies {
    api(project(":core"))
    api(project(":cli"))
    api(project(":web-server"))
}

application {
    mainClassName = "me.devnatan.katan.bootstrap.KatanLauncher"
}

tasks {
    jar {
        manifest {
            attributes["Main-Class"] = application.mainClassName
        }
    }

    shadowJar {
        archiveBaseName.set("Katan-${project.parent?.version ?: project.version}")
    }

    build {
        dependsOn(shadowJar)
    }
}