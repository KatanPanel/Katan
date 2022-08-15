@file:Suppress("DSL_SCOPE_VIOLATION")

plugins {
    application
    alias(libs.plugins.shadowjar)
}

application {
    mainClass.set("org.katan.Application")
}

dependencies {
    implementation(libs.jedis)
    implementation(projects.crypto)
    implementation(projects.configuration)
    implementation(projects.eventsDispatcher)
    implementation(projects.http.httpServer)
    implementation(projects.services.idService)
    implementation(projects.services.unitService)
    implementation(projects.services.accountService)
    implementation(projects.services.authService)
    implementation(projects.services.networkService)
    implementation(projects.services.unitInstanceService)
    implementation(projects.services.dockerUnitInstanceService)
    implementation(projects.services.cacheService)
    implementation(projects.services.databaseService)
}

tasks {
    jar {
        manifest {
            attributes["Main-Class"] = application.mainClass.get()
            attributes["Implementation-Version"] = project.version
        }
    }

    shadowJar {
        archiveBaseName.set("katan")
    }
}
