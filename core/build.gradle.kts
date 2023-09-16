plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.detekt)
    alias(libs.plugins.kotlinter)
    alias(libs.plugins.kotlin.serialization)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.log4j.core)
    implementation(libs.koin.core)
    implementation(libs.ktx.coroutines.core)
    implementation(libs.ktx.datetime)
    implementation(libs.ktx.serialization.core)
    implementation(libs.ktx.serialization.json)
    implementation(libs.koin.core)
    implementation(libs.bcprov)
    testImplementation(kotlin("test"))
    testImplementation(libs.ktx.coroutines.test)
}

configurations {
    implementation {
        exclude(module = "bcpkix-jdk15on")
        exclude(module = "bcprov-jdk15on")
    }
}
