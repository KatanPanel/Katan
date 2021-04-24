import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.21"
}

val projectGroup = "me.devnatan.katan"
val projectVersion = "0.0.1"

group = projectGroup
version = projectVersion

allprojects {
    group = projectGroup
    version = projectVersion

    apply(plugin = "org.jetbrains.kotlin.jvm")

    repositories {
        mavenCentral()
        jcenter()
        maven("https://jitpack.io")
        maven("https://dl.bintray.com/kotlin/exposed")
        maven("http://nexus.devsrsouza.com.br/repository/maven-public/")
    }

    dependencies {
        implementation(kotlin("stdlib"))
        implementation(kotlin("reflect"))
        api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.0-RC2")
    }

    tasks {
        withType<KotlinCompile> {
            kotlinOptions {
                freeCompilerArgs = listOf("-Xopt-in=kotlin.RequiresOptIn", "-Xinline-classes")
                jvmTarget = "1.8"
            }
        }
    }
}