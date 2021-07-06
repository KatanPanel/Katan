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

/**
 * Represents the state of a server, obtained by inspecting its container.
 * @see ServerContainerInspection
 */
enum class ServerState {

    /**
     * The internal server process is dead (aka kill).
     */
    DEAD,

    /**
     * The server has been paused.
     */
    PAUSED,

    /**
     * Server is restarting.
     */
    RESTARTING,

    /**
     * Server is started and running.
     */
    RUNNING,

    /**
     * An error occurred while loading the server.
     * One of the causes may be that the server container has not been found.
     */
    UNLOADED,

    /**
     * The state of the server is unknown.
     */
    UNKNOWN

}

/**
 * Returns `true` if the server is loaded or `false` otherwise.
 */
fun ServerState.isLoaded(): Boolean {
    return this != ServerState.UNLOADED
}

/**
 * Returns `true` if the server is up and running or `false` if it's [isInactive].
 */
fun ServerState.isActive(): Boolean {
    return this == ServerState.RESTARTING || this == ServerState.RUNNING
}

/**
 * Returns `true` if the server is stopped, idle, or in an unknown state or `false` otherwise.
 */
fun ServerState.isInactive(): Boolean {
    return this == ServerState.DEAD || this == ServerState.PAUSED || this == ServerState.UNKNOWN
}