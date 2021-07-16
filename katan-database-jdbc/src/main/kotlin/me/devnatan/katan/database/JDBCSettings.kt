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

package me.devnatan.katan.database

data class JDBCSettings(
    val url: String?,
    val dialect: String?,
    val host: String,
    val user: String,
    val password: String,
    val database: String,
    val properties: Map<String, String>
) : DatabaseSettings {

    companion object {
        const val Url = "url"
        const val Dialect = "dialect"
        const val Host = "host"
        const val User = "user"
        const val Password = "password"
        const val DatabaseName = "database"
        const val Properties = "properties"
    }

    @Suppress("UNCHECKED_CAST", "IMPLICIT_CAST_TO_ANY")
    override fun <T> get(key: String): T? {
        return when (key) {
            Url -> url
            Dialect -> dialect
            Host -> host
            User -> user
            Password -> password
            DatabaseName -> database
            Properties -> properties
            else -> error("Unknown JDBC database settings key: $key")
        } as T
    }

}