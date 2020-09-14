plugins {
    kotlin("jvm") version "1.4.10"
}

group = "me.devnatan.katan"
version = "0.1.0"

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
    }
}