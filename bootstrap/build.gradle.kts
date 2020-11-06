plugins {
    application
    id("com.github.johnrengelman.shadow") version "6.0.0"
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
    jar {
        manifest {
            attributes["Main-Class"] = application.mainClassName
            attributes["Implementation-Version"] = project.version
        }
    }

    shadowJar {
        archiveBaseName.set("Katan")
        archiveClassifier.set(null as String?)

        // fix for "Could not initialize class org.eclipse.jetty.server.HttpConnection"
        mergeServiceFiles()
    }

    build {
        dependsOn(shadowJar)
    }
}