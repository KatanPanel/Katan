plugins {
    alias(libs.plugins.kotlin)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(projects.core)
    implementation(libs.postgresql)
    implementation(libs.bundles.exposed)
    implementation(libs.koin.core)
}

kotlin {
    explicitApi()
}
