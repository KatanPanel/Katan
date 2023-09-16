plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.kotlin.serialization)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(projects.core)
    implementation(libs.ktx.serialization.core)
    implementation(libs.ktx.datetime)
}