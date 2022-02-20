import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version Dependencies.kotlinVersion
    id("org.jetbrains.dokka") version "1.4.10.2"
    `maven-publish`
}

val projectGroup = "me.devnatan.katan"
val projectVersion = "0.0.1"

group = projectGroup
version = projectVersion

buildscript {
    dependencies {

    }
}

subprojects {
    group = projectGroup
    version = projectVersion

    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.dokka")
    apply<MavenPublishPlugin>()

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
        implementation(Dependencies.Koin.core)
        implementation(Dependencies.Koin.slf4j)
        testImplementation(Dependencies.Koin.test)
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

    java {
        withJavadocJar()
        withSourcesJar()
    }

    publishing {
        publications {
            create<MavenPublication>("maven") {
                from(components["java"])

                pom {
                    scm {
                        url.set("https://github.com/KatanPanel/Katan/tree/master/$artifactId")
                    }

                    licenses {
                        license {
                            name.set("The MIT License (MIT)")
                            url.set("https://github.com/KatanPanel/Katan/blob/master/LICENSE")
                        }
                    }
                }
            }
        }
    }
}

tasks.dokkaHtmlMultiModule.configure {
    outputDirectory.set(buildDir.resolve("dokkaCustomMultiModuleOutput"))
}