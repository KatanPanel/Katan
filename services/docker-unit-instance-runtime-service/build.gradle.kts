repositories {
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {
    implementation(libs.dockerJava)
    implementation(libs.dockerJava.transport.okhttp)
    implementation(projects.configuration)
    implementation(projects.services.networkService)
    implementation(projects.services.unitInstanceRuntimeService)
    implementation(libs.yoki.core)
    implementation(libs.yoki.engine.docker)
}