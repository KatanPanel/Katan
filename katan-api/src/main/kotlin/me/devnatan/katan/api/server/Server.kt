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

package me.devnatan.katan.api.server

import me.devnatan.katan.api.game.Game
import me.devnatan.katan.api.server.composition.Compositions

/**
 * Represents a server created by Katan, servers can be
 * composed (using Server Compositions API) and modified dynamically.
 */
interface Server {

    /**
     * Returns the server id.
     */
    val id: Int

    /**
     * Returns the server name.
     */
    var name: String

    /**
     * Returns all accounts that have permissions on that server.
     */
    val holders: MutableSet<ServerHolder>

    /**
     * Returns the [ServerContainer] linked to this server.
     */
    val container: ServerContainer

    /**
     * Returns the current server state.
     */
    var state: ServerState

    /**
     * Returns the server compositions container.
     */
    val compositions: Compositions

    /**
     * Returns the [Game] that this server is targeting.
     */
    val game: ServerGame

    /**
     * Returns the server remote connection address.
     */
    val host: String

    /**
     * Returns the server remote connection port.
     */
    val port: Short

}