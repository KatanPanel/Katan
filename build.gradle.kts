@file:Suppress("UnstableApiUsage")

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kotlin.atomicfu)
}

repositories {
    mavenCentral()
}

subprojects {
    group = "org.katan"
    version = "0.1.0"

    apply(plugin = rootProject.libs.plugins.kotlin.jvm.get().pluginId)
    apply(plugin = rootProject.libs.plugins.kotlin.serialization.get().pluginId)
    apply(plugin = rootProject.libs.plugins.kotlin.atomicfu.get().pluginId)

    repositories {
        mavenCentral()
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

    tasks {
        withType<KotlinCompile> {
            kotlinOptions {
                jvmTarget = "11"
            }
        }
    }
}