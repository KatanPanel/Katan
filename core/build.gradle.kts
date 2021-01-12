val exposedVersion = "0.27.1"
val dockerJavaVersion = "3.2.5"

plugins {
    application
    id("com.github.johnrengelman.shadow") version "6.0.0"
}

dependencies {
    api(project(":common"))
    implementation(project(":web-server"))
    implementation(project(":cli"))
    implementation(project(":file-system"))
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.kotlinx:atomicfu:0.14.4")
    api("com.github.docker-java:docker-java-api:$dockerJavaVersion")
    implementation("com.github.docker-java:docker-java-core:$dockerJavaVersion")
    implementation("com.github.docker-java:docker-java-transport-okhttp:$dockerJavaVersion")
    implementation("com.h2database:h2:1.4.200")
    implementation("mysql:mysql-connector-java:8.0.21")
    implementation("redis.clients:jedis:3.3.0")
    implementation("org.bouncycastle:bcprov-jdk15to18:1.66")
    implementation("org.apache.logging.log4j:log4j-core:2.13.3")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.13.3")
}

configurations {
    implementation {
        exclude(module = "bcpkix-jdk15on")
        exclude(module = "bcprov-jdk15on")
    }
}

application {
    mainClassName = "me.devnatan.katan.core.KatanLauncher"
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

        // fix "Could not initialize class org.eclipse.jetty.server.HttpConnection"
        mergeServiceFiles()
    }

    build {
        dependsOn(shadowJar)
    }
}