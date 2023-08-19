plugins {
    alias(libs.plugins.kotlin)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.ktx.coroutines.core)
    implementation(libs.log4j.core)
    implementation(libs.koin.core)
    testImplementation(kotlin("test"))
    testImplementation(libs.ktx.coroutines.test)
}

kotlin {
    explicitApi()
}