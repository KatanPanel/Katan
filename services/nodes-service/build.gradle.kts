plugins {
    alias(libs.plugins.kotlin)
}

repositories {
    mavenCentral()
}

kotlin {
    explicitApi()
}