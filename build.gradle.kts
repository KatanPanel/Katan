import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.10"
}

group = "me.devnatan.katan"
version = "0.1.0"

allprojects {
    repositories {
        maven("https://dl.bintray.com/kotlin/exposed")
        maven("http://nexus.devsrsouza.com.br/repository/maven-public/")
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xopt-in=kotlin.RequiresOptIn")
            jvmTarget = "1.8"
        }
    }
}

val log4jVersion = "2.13.3"

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")

    repositories {
        mavenCentral()
    }

    dependencies {
        implementation(kotlin("stdlib"))
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.9")
        implementation("io.ktor:ktor-websockets:1.4.0")
        implementation("com.github.docker-java:docker-java:3.2.5")
        implementation("org.apache.logging.log4j:log4j-api:$log4jVersion")
        implementation("org.apache.logging.log4j:log4j-core:$log4jVersion")
        implementation("org.apache.logging.log4j:log4j-slf4j-impl:$log4jVersion")
    }
}