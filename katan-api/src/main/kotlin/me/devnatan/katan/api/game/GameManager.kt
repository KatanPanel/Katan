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

package me.devnatan.katan.api.game

/**
 * It represents the game handler of Katan, through it it is possible to register
 * new [Game]s making the range of games supported by Katan beyond the native ones grow.
 */
interface GameManager {

    /**
     * Returns all supported games.
     */
    fun getRegisteredGames(): Collection<Game>

    /**
     * Returns a supported [Game] from its [name] (case-insensitive) or `null` if not found.
     * @param name the game name.
     */
    fun getGame(name: String): Game?

    /**
     * Returns whether the game with the specified [name] is supported by Katan.
     * @param name the game name.
     */
    fun isSupported(name: String): Boolean

    /**
     * Register a new game.
     * @param game the game to be registered.
     */
    suspend fun registerGame(game: Game)

    /**
     * Unregisters a game.
     * @param game the game to be unregistered.
     */
    suspend fun unregisterGame(game: Game)

}