# Katan
[![Open Source](https://badges.frapsoft.com/os/v1/open-source.svg?v=103)](https://github.com/ellerbrock/open-source-badges/)
[![Stars](https://img.shields.io/github/stars/DevNatan/Katan.svg?color=1bcc1b)](https://github.com/hs-org/Katan/stargazers)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/a9844adeafb449f487368a84f5eb1df5)](https://www.codacy.com/app/hs-org/Katan?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=DevNatan/Katan&amp;utm_campaign=Badge_Grade)

Katan is a fast and simple Minecraft server management panel built with [Ktor](https://ktor.io/) and [Vue.js](https://vuejs.org/).\
All Katan servers are isolated in Docker containers that are handled constantly.

This repository is only about the Katan server, if you are looking for the client, [here it is](https://github.com/hs-org/Katan-Panel).

## Summary
  * [System requirements](#system-requirements)
  * [Build setup](#build-setup)
  * [Getting started](#getting-started)
    * [Configuration](#configuration)
  * [Contributing](#contributing)
  * [Issue reporting](#issue-reporting)
  * [License](#license)

## System requirements
To run it is necessary that you have the following contents installed:
* [Docker](https://www.docker.com/)
* [JDK](https://www.oracle.com/br/java/technologies/javase-downloads.html) 1.8 or higher.
* A [MySQL](https://www.mysql.com/) server

If you are thinking of contributing, you must have [Kotlin](http://github.com/JetBrains/Kotlin) v1.3.72 or higher.

## Build setup
You can get a file from the Katan server in the repository's [releases](https://github.com/hs-org/Katan/releases) tab.\
Or, clone the repository and build it. Remembering: we don't support code that doesn't exist in the repository (modified locally).
```bash
$ java -jar Katan-0.1.0-RELEASE-latest.jar
```

Recommended initialization parameters:
* [`-Xms1G -Xmx4G`](https://docs.oracle.com/cd/E15523_01/web.1111/e13814/jvm_tuning.htm#PERFM164): specifies the memory allocation pool for the JVM.
* [`-XX:+UseStringDeduplication`](http://openjdk.java.net/jeps/192): optimizes the heap memory by removing duplicate String values to a global single char[] array.
* [`-XX:+UseG1GC`](https://www.oracle.com/technetwork/tutorials/tutorials-1876574.html): uses the Garbage Collector designed for **multi-processor machines** with **large memory space** (do not put it on if you are not aware of your machine).
* [`-XX:+UseGCOverheadLimit`](https://www.baeldung.com/java-gc-overhead-limit-exceeded): limits the proportion of the VM's time that is spent in GC before an OutOfMemory error
* [`-XX:+HeapDumpOnOutOfMemoryError`](https://docs.oracle.com/javase/8/docs/technotes/guides/troubleshoot/clopts001.html): dump GC heap into physical file in case of OutOfMemoryError

You can explore all the initialization parameters available to the JVM [here](https://www.oracle.com/java/technologies/javase/vmoptions-jsp.html).

## Getting started
After starting the Katan for the first time you will need to configure it correctly.\
Look for the `katan.json` file in the root directory, open it.

### Configuration
All configuration keys are mandatory so if any is missing Katan will not be able to start.
There is no explanation of the configuration in the configuration file so we will explain them:

| Key | Schema | Default Value | Description |
| --- | ------ | ------------- | ----------- |
| env | "development", "production" | production | Development environment, may varies depending on what stage the Katan is in. |
| auth.token |  | katan-random-token | Token **synchronized with the client** to access server resources. |
| auth.secret | | katan-super-secret-secret | Secret used for JWT strategies and password encryption. |
| mysql.url | protocol//\[hosts]\[/database]\[?properties] | jdbc:mysql://localhost:3306/katan?useSSL=true | MySQL server URL. |
| mysql.driver | [Java package naming](https://docs.oracle.com/javase/tutorial/java/package/namingpkgs.html) | com.mysql.jdbc.Driver | MySQL connector. |
| mysql.user | | root | MySQL server username credential. |
| mysql.password | | "" | MySQL server password credential. |
| docker.host | [protocol]//\[host](:port) | tcp://localhost:2375 | Docker host protocol address. |

> **Attention:** There are other flat files within the Katan JAR like `logback.xml` and `application.conf` but it is not recommended to modify them.

## Contributing
Pull requests are totally accepted and thanks for doing them.\
But, rather, it is necessary some requirements for it to be accepted.
See [How to contribute](/CONTRIBUTING).

## Issue reporting
You can get support by creating an [issue](https://github.com/hs-org/Katan/issues).

## License
Katan is released under [Apache License 2.0](/LICENSE).
```text
Copyright 2019-2020 Happy Shop.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
