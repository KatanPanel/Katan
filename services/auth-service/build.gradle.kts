dependencies {
    implementation(libs.javaJwt)
    implementation(libs.bcprov)
    implementation(projects.services.accountService)
}

configurations {
    implementation {
        exclude(module = "bcpkix-jdk15on")
        exclude(module = "bcprov-jdk15on")
    }
}