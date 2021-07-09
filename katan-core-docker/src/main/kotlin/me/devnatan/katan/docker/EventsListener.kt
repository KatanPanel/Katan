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

package me.devnatan.katan.docker

import br.com.devsrsouza.eventkt.EventScope
import br.com.devsrsouza.kotlin.docker.apis.SystemApi
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import me.devnatan.katan.api.event.server.ServerEvent
import me.devnatan.katan.api.event.server.ServerStartEvent
import me.devnatan.katan.api.event.server.ServerStopEvent
import me.devnatan.katan.api.server.Server
import me.devnatan.katan.api.server.ServerManager
import me.devnatan.katan.docker.util.Attributes
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.Duration
import java.time.Instant

class EventsListener : KoinComponent, CoroutineScope by CoroutineScope(Dispatchers.IO + CoroutineName("Docker Events Listener"))  {

    private val eventScope by inject<EventScope>()
    private val serverManager by inject<ServerManager>()

    private val systemApi = SystemApi(serializer = Json {
        ignoreUnknownKeys = true
    })

    fun listen() = launch {
        systemApi.systemEvents(
            since = null,
            until = null,
            filters = "{\"type\": [\"container\"]}"
        ).collect { event ->
            val serverId = event.actor?.attributes?.get(Attributes.SERVER_ID)?.toInt() ?: return@collect
            val action = event.action!!
            val timestamp = event.time!!.toLong()

            when (action) {
                "start" -> onServerStart(serverId, timestamp)
                "stop" -> onServerStop(serverId, timestamp)
                "pause", "unpause", "kill", "die", "oom" -> {
                    // not treated for now, just update server status
                    serverManager.inspectServer(serverManager.getServer(serverId))
                }
            }
        }
    }

    private fun timestampToDuration(
        timestamp: Long
    ) = Duration.ofMillis(Instant.now().toEpochMilli() - timestamp)

    private inline fun publishAndInspect(
        jobName: String,
        serverId: Int,
        crossinline event: (Server) -> ServerEvent
    ) = launch(CoroutineName("server $jobName")) {
        val target = serverManager.getServer(serverId)
        eventScope.publish(event(target))
        serverManager.inspectServer(target)
    }

    private fun onServerStart(
        serverId: Int,
        timestamp: Long
    ) = publishAndInspect("start", serverId) {
        ServerStartEvent(it, duration = timestampToDuration(timestamp))
    }

    private fun onServerStop(
        serverId: Int,
        timestamp: Long
    ) = publishAndInspect("stop", serverId) {
        ServerStopEvent(it, duration = timestampToDuration(timestamp))
    }

}