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

package me.devnatan.katan.bootstrap

import me.devnatan.katan.core.CoreModule
import me.devnatan.katan.webserver.WsModule
import org.koin.core.context.startKoin
import org.koin.logger.slf4jLogger

fun main() {
    startKoin {
        slf4jLogger()
        modules(CoreModule, WsModule)
    }
}

/* fun getEnv() {
    val currEnv = (System.getenv(EnvKeys.ENVIRONMENT) ?: KatanEnvironment.PRODUCTION).toLowerCase()

    // check if it is a valid environment mode
    if (currEnv !in KatanEnvironment.ALL) {
        System.err.println("Unknown environment mode: $currEnv")
        exitProcess(0)
    }

    val env = KatanEnvironment(currEnv)

    // enable coroutines debug in development mode
    if (env.isDevelopment()) {
        System.setProperty(DEBUG_PROPERTY_NAME, DEBUG_PROPERTY_VALUE_ON)
    }

    System.setProperty(
        Katan.LOG_LEVEL_PROPERTY,
        it.defaultLogLevel().toString()
    )
    System.setProperty(
        Katan.LOG_PATTERN_PROPERTY, if (it.isProduction())
            "[%d{yyyy-MM-dd HH:mm:ss}] [%-4level]: %msg%n"
        else
            "[%d{yyyy-MM-dd HH:mm:ss}] [%t/%-4level @ %logger{1}]: %msg%n"
    )

    return env
} */