plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.detekt)
    alias(libs.plugins.kotlinter)
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