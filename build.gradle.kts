import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.10"
}

val projectGroup = "me.devnatan.katan"
val projectVersion = "0.1.0"

group = projectGroup
version = projectVersion

val log4jVersion = "2.13.3"
allprojects {
    group = projectGroup
    version = projectVersion

    apply(plugin = "org.jetbrains.kotlin.jvm")

    repositories {
        mavenCentral()
        jcenter()
        maven("https://dl.bintray.com/kotlin/exposed")
        maven("http://nexus.devsrsouza.com.br/repository/maven-public/")
    }

    dependencies {
        implementation(kotlin("stdlib"))
        implementation(kotlin("reflect"))
        implementation("br.com.devsrsouza.eventkt:eventkt-core-jvm:0.1.0-SNAPSHOT")
        implementation("com.typesafe:config:1.4.0")
        implementation("org.apache.logging.log4j:log4j-api:$log4jVersion")
        implementation("org.apache.logging.log4j:log4j-core:$log4jVersion")
        implementation("org.apache.logging.log4j:log4j-slf4j-impl:$log4jVersion")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.9")
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