plugins {
    alias(libs.plugins.kotlin)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(projects.model)
    implementation(libs.koin.core)
}

kotlin {
    explicitApi()
}