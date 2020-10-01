import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.10"
}

group = "me.devnatan.katan"
version = "0.1.0"

val log4jVersion = "2.13.3"

allprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")

    repositories {
        maven("https://dl.bintray.com/kotlin/exposed")
        maven("http://nexus.devsrsouza.com.br/repository/maven-public/")
        maven("https://dl.bintray.com/marshallpierce/maven")
        mavenCentral()
        jcenter()
    }

    dependencies {
        api("com.typesafe:config:1.4.0")
        api("br.com.devsrsouza.eventkt:eventkt-core-jvm:0.1.0-SNAPSHOT")
        api("org.apache.logging.log4j:log4j-api:$log4jVersion")
        api("org.apache.logging.log4j:log4j-core:$log4jVersion")
        api("org.apache.logging.log4j:log4j-slf4j-impl:$log4jVersion")
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

subprojects {
    dependencies {
        implementation(kotlin("stdlib"))
        implementation(kotlin("reflect"))
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.9")
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.0-RC2")
    }
}