plugins {
    application
    alias(libs.plugins.shadowjar)
}

application {
    mainClass.set("org.katan.Application")
}

dependencies {
    implementation(projects.core)
    implementation(projects.runtime)
    implementation(projects.services.idService)
    implementation(projects.services.accountService)
    implementation(projects.services.unitService)
    implementation(projects.services.unitInstanceService)
    implementation(projects.configuration)
}

tasks {
    jar {
        manifest {
            attributes["Main-Class"] = application.mainClass.get()
            attributes["Implementation-Version"] = project.version
        }
    }
}