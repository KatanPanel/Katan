
dependencies {
    implementation(project(":katan-api"))
    implementation(project(":katan-database"))
    implementation(Dependencies.Exposed.core)
    implementation(Dependencies.Exposed.dao)
    implementation(Dependencies.Exposed.jdbc)
    implementation(Dependencies.Exposed.javaTime)
    implementation(Dependencies.AtomicFU.jvmArtifact)
}