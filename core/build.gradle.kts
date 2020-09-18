val ktorVersion = "1.4.0"
val exposedVersion = "0.27.1"
val log4jVersion = "2.13.3"

dependencies {
    api(project(":api"))
    implementation("com.auth0:java-jwt:3.10.3")
    api("org.jetbrains.exposed:exposed-core:$exposedVersion")
    api("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    api("org.jetbrains.exposed:exposed-java-time:$exposedVersion")
    api("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    api("com.fasterxml.jackson.core:jackson-core:2.10.2")
    implementation("br.com.devsrsouza.eventkt:eventkt-core-jvm:0.1.0-SNAPSHOT")
    implementation("org.jetbrains.kotlinx:atomicfu:0.14.4")
    api("com.typesafe:config:1.4.0")
    implementation("mysql:mysql-connector-java:8.0.21")
    api("io.ktor:ktor-websockets:$ktorVersion")
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    api("io.ktor:ktor-jackson:$ktorVersion")
    implementation("com.github.docker-java:docker-java:3.2.5")
    implementation("org.apache.logging.log4j:log4j-api:$log4jVersion")
    implementation("org.apache.logging.log4j:log4j-core:$log4jVersion")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:$log4jVersion")
}