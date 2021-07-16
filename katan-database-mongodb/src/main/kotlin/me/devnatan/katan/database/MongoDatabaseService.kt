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

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.MongoCredential
import com.mongodb.reactivestreams.client.ClientSession
import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.MongoDatabase
import kotlinx.coroutines.*
import org.litote.kmongo.coroutine.commitTransactionAndAwait
import org.litote.kmongo.reactivestreams.KMongo
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class MongoDatabaseService : DatabaseService<ClientSession>() {

    private lateinit var client: MongoClient
    val database: MongoDatabase by lazy { client.getDatabase("katan") }

    override suspend fun connect(settings: DatabaseSettings) {
        checkDatabaseSettings<MongoSettings>(settings)
        client = KMongo.createClient(MongoClientSettings.builder()
            .applicationName("Katan")
            .applyConnectionString(ConnectionString(settings.connectionString))
            .credential(MongoCredential.createCredential(settings.userName, settings.userDatabase, settings.password.toCharArray()))
            .retryWrites(true)
            .build())
    }

    override suspend fun close() {
        cancel()
        client.close()
    }

    override suspend fun <R> transaction(dispatcher: CoroutineDispatcher, block: suspend ClientSession.() -> R): R {
        return suspendCancellableCoroutine { cont ->
            client.startSession().subscribe(object: Subscriber<ClientSession> {
                private var sub: Subscription? = null

                override fun onSubscribe(subscription: Subscription) {
                    sub = subscription
                    cont.invokeOnCancellation { subscription.cancel() }
                    subscription.request(Long.MAX_VALUE)
                }

                override fun onNext(session: ClientSession) {
                    val subscription = sub ?: error("'onNext' called before subscription")

                    session.startTransaction()
                    launch(NonCancellable, CoroutineStart.UNDISPATCHED) {
                        val value = block(session)
                        session.commitTransactionAndAwait()
                        subscription.cancel()
                        cont.resume(value)
                    }
                }

                override fun onComplete() {
                }

                override fun onError(error: Throwable) {
                    if (sub == null)
                        error("'onError' called before subscription")
                    cont.resumeWithException(error)
                }
            })
        }
    }

}