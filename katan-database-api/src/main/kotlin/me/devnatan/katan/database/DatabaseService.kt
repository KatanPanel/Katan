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

import kotlinx.coroutines.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.coroutines.CoroutineContext

abstract class DatabaseService<T> : CoroutineScope {

    override val coroutineContext: CoroutineContext = SupervisorJob() + CoroutineName("Database Service")

    abstract suspend fun connect(settings: DatabaseSettings)

    abstract suspend fun <R> transaction(dispatcher: CoroutineDispatcher, block: suspend T.() -> R): R

    open suspend fun close() {
    }

}

suspend fun <T, R> DatabaseService<T>.transaction(
    block: suspend T.() -> R
): R = transaction(Dispatchers.IO, block)

@OptIn(ExperimentalContracts::class)
inline fun <reified T : DatabaseSettings> checkDatabaseSettings(settings: DatabaseSettings) {
    contract {
        returns() implies (settings is T)
    }

    if (settings !is T)
        throw IllegalArgumentException("Unsupported database settings type")
}