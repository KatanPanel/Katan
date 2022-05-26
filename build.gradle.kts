@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    kotlin("jvm") version libs.versions.kotlin.get()
    alias(libs.plugins.serialization)
}

repositories {
    mavenCentral()
}

subprojects {
    group = "org.katan"
    version = "0.1.0"

    apply(plugin = "org.jetbrains.kotlin.jvm")

    repositories {
        mavenCentral()
    }

    dependencies {
        implementation(kotlin("stdlib"))
        implementation(rootProject.libs.ktx.atomicfu)
        implementation(kotlin("reflect"))
        testImplementation("org.jetbrains.kotlin:kotlin-test:1.6.10") // TODO version catalogs
        implementation(rootProject.libs.ktx.coroutines.core)
        implementation(rootProject.libs.ktx.serialization.core)
        implementation(rootProject.libs.koin.core)
        implementation(rootProject.libs.ktx.datetime)
        implementation(rootProject.libs.log4j.core)
    }

    tasks {
        withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
            kotlinOptions {
                freeCompilerArgs = listOf("-Xopt-in=kotlin.RequiresOptIn", "-Xinline-classes")
                jvmTarget = "1.8"
            }
        }
    }
}