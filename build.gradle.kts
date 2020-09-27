import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.10"
    kotlin("plugin.serialization") version "1.4.10"
}

group = "me.devnatan.katan"
version = "0.1.0"

allprojects {
    repositories {
        maven("https://dl.bintray.com/kotlin/exposed") // exposed
        maven("http://nexus.devsrsouza.com.br/repository/maven-public/") // eventkt-core-jvm
        maven("https://dl.bintray.com/marshallpierce/maven") // ktor-csrf
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xopt-in=kotlin.RequiresOptIn")
            jvmTarget = "1.8"
        }
    }
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "kotlinx-serialization")

    repositories {
        mavenCentral()
        jcenter()
    }

    dependencies {
        implementation(kotlin("stdlib"))
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.9")
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.0-RC2")
    }
}