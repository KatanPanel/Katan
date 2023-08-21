plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.detekt)
    alias(libs.plugins.kotlinter)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.ktx.datetime)
    implementation(libs.ktx.serialization.core)
    implementation(libs.ktx.serialization.json)
    implementation(libs.koin.core)
}

kotlin {
    explicitApi()
}