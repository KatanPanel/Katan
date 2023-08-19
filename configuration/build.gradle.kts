plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.detekt)
    alias(libs.plugins.kotlinter)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.koin.core)
}

kotlin {
    explicitApi()
}
