plugins {
    alias(libs.plugins.kotlin)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(projects.core)
    implementation(libs.yoki)
    implementation(libs.log4j.core)
    implementation(libs.koin.core)
    implementation(libs.ktx.coroutines.core)
}

kotlin {
    explicitApi()
}