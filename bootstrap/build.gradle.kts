plugins {
    application
    id("com.github.johnrengelman.shadow") version "6.0.0"
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
            attributes["Implementation-Version"] = project.version
        }
    }

    shadowJar {
        archiveBaseName.set("Katan")

        // fix for "Could not initialize class org.eclipse.jetty.server.HttpConnection"
        mergeServiceFiles()
    }

    build {
        dependsOn(shadowJar)
    }
}