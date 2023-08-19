plugins {
    alias(libs.plugins.kotlin)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(projects.model)
    implementation(libs.bcprov)
    implementation(libs.koin.core)
}

configurations {
    implementation {
        exclude(module = "bcpkix-jdk15on")
        exclude(module = "bcprov-jdk15on")
    }
}

kotlin {
    explicitApi()
}
