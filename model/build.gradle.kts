plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.kotlin.serialization)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.ktx.datetime)
    implementation(libs.ktx.serialization.core)
}

kotlin {
    explicitApi()
}