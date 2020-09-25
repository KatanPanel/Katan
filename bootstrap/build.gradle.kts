plugins {
    application
    id("com.github.johnrengelman.shadow") version "5.0.0"
}

dependencies {
    implementation(project(":core"))
    implementation(project(":cli"))
    implementation(project(":web-server"))
}

application {
    mainClassName = "me.devnatan.katan.bootstrap.KatanLauncher"
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