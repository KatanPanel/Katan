dependencies {
    implementation(libs.bcprov)
    implementation(projects.model)
}

configurations {
    implementation {
        exclude(module = "bcpkix-jdk15on")
        exclude(module = "bcprov-jdk15on")
    }
}
