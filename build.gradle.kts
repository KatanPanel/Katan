@file:Suppress("UnstableApiUsage", "DSL_SCOPE_VIOLATION")

import com.diffplug.gradle.spotless.SpotlessExtension
import com.gorylenko.GitPropertiesPluginExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kotlin.atomicfu) apply false
    alias(libs.plugins.spotless) apply false
    id("com.gorylenko.gradle-git-properties") version "2.4.1"
}

repositories {
    mavenCentral()
}

subprojects {
    group = "org.katan"
    version = "0.1.0-SNAPSHOT"

    apply(plugin = rootProject.libs.plugins.kotlin.jvm.get().pluginId)
    apply(plugin = rootProject.libs.plugins.kotlin.serialization.get().pluginId)
    apply(plugin = rootProject.libs.plugins.kotlin.atomicfu.get().pluginId)
    apply(plugin = rootProject.libs.plugins.spotless.get().pluginId)

    repositories {
        mavenCentral()
        // for Yoki
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    }

    dependencies {
        implementation(kotlin("stdlib"))
        implementation(kotlin("reflect"))
        implementation(rootProject.libs.ktx.coroutines.core)
        implementation(rootProject.libs.ktx.serialization.core)
        implementation(rootProject.libs.koin.core)
        implementation(rootProject.libs.ktx.datetime)
        implementation(rootProject.libs.log4j.core)
        compileOnly(rootProject.libs.ktx.atomicfu)
        runtimeOnly(rootProject.libs.ktx.atomicfu)
        testImplementation(rootProject.libs.kotlin.test)
    }

    configure<SpotlessExtension> {
        kotlin {
            ktlint()
        }

        kotlinGradle {
            ktlint()
        }
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    tasks {
        withType<KotlinCompile> {
            kotlinOptions {
                jvmTarget = "11"
            }
        }
    }
}

configure<GitPropertiesPluginExtension> {
    dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    gitPropertiesName = "build.properties"
    gitPropertiesResourceDir.set(file("${project.rootDir}/application/build/resources/main"))
}
