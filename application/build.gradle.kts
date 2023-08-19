plugins {
    application
    alias(libs.plugins.shadowjar)
    alias(libs.plugins.kotlin)
    alias(libs.plugins.detekt)
    alias(libs.plugins.kotlinter)
}

application {
    mainClass.set("org.katan.Application")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(projects.crypto)
    implementation(projects.configuration)
    implementation(projects.eventsDispatcher)
    implementation(projects.http.httpServer)
    implementation(projects.http.httpClient)
    implementation(projects.services.idService)
    implementation(projects.services.unitService)
    implementation(projects.services.accountService)
    implementation(projects.services.authService)
    implementation(projects.services.networkService)
    implementation(projects.services.instanceService)
    implementation(projects.services.cacheService)
    implementation(projects.services.databaseService)
    implementation(projects.services.hostFsService)
    implementation(projects.services.blueprintService)
    implementation(libs.yoki)
    implementation(libs.log4j.core)
    implementation(libs.koin.core)
    implementation(libs.ktx.coroutines.core)
    implementation(libs.exposed.core)
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
