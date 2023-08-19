plugins {
    alias(libs.plugins.kotlin)
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
