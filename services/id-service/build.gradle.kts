plugins {
    alias(libs.plugins.kotlin)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(projects.core)
    implementation(libs.snowflakeId)
    implementation(libs.hibernateValidator)
    implementation(libs.koin.core)
}

kotlin {
    explicitApi()
}
