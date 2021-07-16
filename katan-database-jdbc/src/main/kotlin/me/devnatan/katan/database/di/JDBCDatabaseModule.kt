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

package me.devnatan.katan.database.di

import me.devnatan.katan.database.repository.AccountsRepository
import me.devnatan.katan.database.repository.JDBCAccountsRepository
import me.devnatan.katan.database.repository.JDBCServersRepository
import me.devnatan.katan.database.repository.ServersRepository
import org.koin.dsl.bind
import org.koin.dsl.module

val JDBCDatabaseModule = module {
    single { JDBCAccountsRepository(get(), get()) } bind AccountsRepository::class
    single { JDBCServersRepository(get(), get()) } bind ServersRepository::class
}