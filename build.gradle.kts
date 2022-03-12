import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version Dependencies.kotlinVersion
    id("org.jetbrains.dokka") version "1.4.10.2"
}

val projectGroup = "me.devnatan.katan"
val projectVersion = "0.0.1"

group = projectGroup
version = projectVersion

repositories {
    mavenCentral()
}

subprojects {
    group = projectGroup
    version = projectVersion

    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.dokka")

    repositories {
        mavenCentral()
        maven(Repositories.jitpack)
        maven(Repositories.kotlinExposed)
    }

    dependencies {
        implementation(kotlin("stdlib"))
        implementation(kotlin("reflect"))
        implementation(rootProject.libs.ktx.coroutines.core)
        implementation(rootProject.libs.ktx.serialization.json)
        implementation(rootProject.libs.koin.core)
        testImplementation(rootProject.libs.koin.test)
        implementation(rootProject.libs.ktx.datetime)
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

//tasks.dokkaHtmlMultiModule.configure {
//    outputDirectory.set(buildDir.resolve("dokkaCustomMultiModuleOutput"))
//}