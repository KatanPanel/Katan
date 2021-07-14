/*
 * Copyright 2020-present Natan Vieira do Nascimento
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

plugins {
    `maven-publish`
    id("org.jetbrains.dokka") version "1.4.10.2"
}

dependencies {
    api("com.typesafe:config:1.4.0")
    api("br.com.devsrsouza.eventkt:eventkt-core-jvm:0.1.0-SNAPSHOT")
    api("org.slf4j:slf4j-api:1.7.30")
    api(Dependencies.config4k)
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

                licenses {
                    license {
                        name.set("The MIT License (MIT)")
                        url.set("https://github.com/KatanPanel/Katan/blob/master/LICENSE")
                    }
                }
            }
        }
    }
}