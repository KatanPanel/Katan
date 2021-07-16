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

package me.devnatan.katan.core

import com.typesafe.config.ConfigFactory
import io.github.config4k.extract
import me.devnatan.katan.api.KatanEnvironment
import me.devnatan.katan.common.EnvKeys
import me.devnatan.katan.config.KatanConfig
import org.koin.core.component.KoinComponent
import java.io.File

class KatanCore2(val env: KatanEnvironment) : KoinComponent {

    val config: KatanConfig

    init {
        val rootDir = File(System.getenv(EnvKeys.ROOT_DIR) ?: System.getProperty("user.dir"), "katan")
        config = ConfigFactory.parseFile(File(rootDir, "katan.conf")).extract()
    }

    suspend fun start() {
    }

}