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

package me.devnatan.katan.database.repository

import com.mongodb.reactivestreams.client.ClientSession
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import me.devnatan.katan.api.security.account.Account
import me.devnatan.katan.api.security.account.AccountFactory
import me.devnatan.katan.api.security.role.RoleFactory
import me.devnatan.katan.database.DatabaseService
import me.devnatan.katan.database.MongoDatabaseService
import me.devnatan.katan.database.entity.MongoAccountEntity
import me.devnatan.katan.database.entity.MongoRoleEntity
import org.litote.kmongo.coroutine.toList
import org.litote.kmongo.eq
import org.litote.kmongo.reactivestreams.getCollection
import org.litote.kmongo.set
import org.litote.kmongo.setTo
import java.util.*

class MongoAccountsRepository(
    databaseService: DatabaseService<ClientSession>,
    private val accountFactory: AccountFactory,
    private val roleFactory: RoleFactory
) : AccountsRepository {

    private val collection = (databaseService as MongoDatabaseService).database.getCollection<MongoAccountEntity>()

    override suspend fun init() {
    }

    override suspend fun findAll(): Collection<Account> {
        return collection.find().toList().map { entity -> transform(entity) }
    }

    override suspend fun findOne(id: UUID): Account? {
        return collection.find(MongoAccountEntity::id eq id).awaitFirstOrNull()?.let { entity ->
            transform(entity)
        }
    }

    override suspend fun insert(account: Account) {
        collection.insertOne(MongoAccountEntity(
            account.id, account.username, account.registeredAt, account.lastLogin, account.role?.let { role ->
                MongoRoleEntity(role.id, role.name, role.createdAt)
            }
        )).awaitFirst()
    }

    override suspend fun update(account: Account) {
        collection.updateOne(
            MongoAccountEntity::id eq account.id,
            set(MongoAccountEntity::lastLogin.setTo(account.lastLogin))
        ).awaitFirst()
    }

    private fun transform(entity: MongoAccountEntity): Account {
        return accountFactory.create(entity.id, entity.username, entity.registeredAt).apply {
            lastLogin = entity.lastLogin
            entity.role?.let { role = roleFactory.create(it.id, it.name, it.createdAt) }
        }
    }

}