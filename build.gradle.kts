@file:Suppress("UnstableApiUsage", "DSL_SCOPE_VIOLATION")

import com.diffplug.gradle.spotless.SpotlessExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kotlin.atomicfu) apply false
    alias(libs.plugins.spotless) apply false
}

repositories {
    mavenCentral()
}

subprojects {
    group = "org.katan"
    version = "0.1.0-SNAPSHOT"

    apply(plugin = libs.plugins.kotlin.jvm.get().pluginId)
    apply(plugin = libs.plugins.kotlin.serialization.get().pluginId)
    apply(plugin = libs.plugins.kotlin.atomicfu.get().pluginId)
    apply(plugin = libs.plugins.spotless.get().pluginId)

    repositories {
        mavenCentral()
    }

    dependencies {
        implementation(kotlin("stdlib"))
        implementation(kotlin("reflect"))
        implementation(libs.ktx.coroutines.core)
        implementation(libs.ktx.serialization.core)
        implementation(libs.koin.core)
        implementation(libs.ktx.datetime)
        implementation(libs.log4j.core)
        compileOnly(libs.ktx.atomicfu)
        runtimeOnly(libs.ktx.atomicfu)
        testImplementation(libs.kotlin.test)
    }

    configure<SpotlessExtension> {
        kotlin {
            ktlint()
        }

        kotlinGradle {
            ktlint()
        }
    }

    tasks {
        withType<KotlinCompile> {
            kotlinOptions {
                jvmTarget = "11"
            }
        }
    }
}
