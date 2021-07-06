import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version Dependencies.kotlinVersion
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
        maven(Repositories.kotlinExposed)
        maven(Repositories.eventKt)
    }

    dependencies {
        implementation(kotlin("stdlib"))
        implementation(kotlin("reflect"))
        implementation(Dependencies.Coroutines.coreArtifact)
        implementation(Dependencies.Serialization.jsonArtifact)
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