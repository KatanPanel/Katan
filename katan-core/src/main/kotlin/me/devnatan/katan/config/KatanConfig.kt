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

package me.devnatan.katan.config

data class KatanConfig(
    val locale: String,
    val timezone: String,
    val docker: Docker,
    val redis: Redis,
    val database: Database,
    val security: Security
) {

    data class Docker(val host: String, val tls: TLS, val properties: Properties) {

        data class TLS(val verify: Boolean, val certPath: String)
        data class Properties(val connectTimeout: Long, val readTimeout: Long)

    }

    data class Redis(val use: Boolean, val host: String)

    data class Database(
        val dialect: String,
        val host: String,
        val database: String,
        val credentials: Credentials,
        val properties: Map<String, Any>
    ) {

        data class Credentials(val user: String, val password: String)

    }

    data class Security(
        val fileSystem: FileSystem,
        val crypto: Crypto
    ) {

        data class FileSystem(val allowUntrustedAccess: Boolean)

        data class Crypto(val hash: String, val allowExternalHashProvider: Boolean)

    }

}