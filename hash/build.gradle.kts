plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.detekt)
    alias(libs.plugins.kotlinter)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(projects.model)
    implementation(libs.bcprov)
    implementation(libs.koin.core)
}



kotlin {
    explicitApi()
}
