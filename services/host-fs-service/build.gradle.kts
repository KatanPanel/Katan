plugins {
    alias(libs.plugins.kotlin)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(projects.core)
    implementation(projects.services.fsService)
    implementation(libs.yoki)
    implementation(libs.koin.core)
    implementation(libs.log4j.core)
    implementation(libs.ktx.datetime)
    implementation(libs.ktx.coroutines.core)
}

kotlin {
    explicitApi()
}
