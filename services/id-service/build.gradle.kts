plugins {
    alias(libs.plugins.kotlin)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(projects.model)
    implementation(projects.configuration)
    implementation(libs.snowflakeId)
    implementation(libs.hibernateValidator)
    implementation(libs.koin.core)
}

kotlin {
    explicitApi()
}
