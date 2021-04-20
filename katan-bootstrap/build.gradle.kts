plugins {
    application
    id("com.github.johnrengelman.shadow") version "6.0.0"
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
        archiveFileName.set("Katan.${archiveExtension.get()}")

        // fix "Could not initialize class org.eclipse.jetty.server.HttpConnection"
        mergeServiceFiles()
    }

    build {
        dependsOn(shadowJar)
    }
}

dependencies {
    implementation(project(":katan-core"))
    implementation(project(":katan-cli"))
    implementation(project(":katan-web-server"))
}