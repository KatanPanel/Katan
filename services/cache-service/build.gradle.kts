plugins {
    alias(libs.plugins.kotlin)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(projects.core)
    implementation(libs.jedis)
    implementation(libs.log4j.core)
    implementation(libs.koin.core)
}

kotlin {
    explicitApi()
}