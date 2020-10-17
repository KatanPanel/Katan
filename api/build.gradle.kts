plugins {
    `maven-publish`
}

dependencies {
    implementation("com.github.ajalt.clikt:clikt-jvm:3.0.1")
    api("com.typesafe:config:1.4.0")
    implementation("org.slf4j:slf4j-simple:1.8.0-beta2")
}

publishing {
    publications {
        val sources by tasks.registering(Jar::class) {
            archiveClassifier.set("sources")
            from(sourceSets.main.get().allSource)
        }

        create<MavenPublication>("maven") {
            from(components["kotlin"])
            artifact(sources.get())

            pom {
                scm {
                    url.set("https://github.com/KatanPanel/Katan/tree/master/api")
                }
            }
        }
    }
}