import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val ktorVersion = "1.4.0"
var exposedVersion = "0.27.1"

repositories {
    maven("https://dl.bintray.com/kotlin/exposed")
    maven("http://nexus.devsrsouza.com.br/repository/maven-public/")
}

dependencies {
    implementation(project(":api"))
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("com.auth0:java-jwt:3.10.3")
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")
    implementation("com.fasterxml.jackson.core:jackson-core:2.10.2")
    implementation("br.com.devsrsouza.eventkt:eventkt-core-jvm:0.1.0-SNAPSHOT") // jvm only!!
    implementation("org.jetbrains.kotlinx:atomicfu:0.14.4")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xopt-in=kotlin.RequiresOptIn")
        jvmTarget = "1.8"
    }
}