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
 * Games are one of the main entities present in the Katan ecosystem, they
 * are used as an information base for the creation of a [Server].
 *
 * Each server must target a [Game], it will also serve as a direct manipulator
 * of the properties of that server in addition to serving as a limiter for
 * server environment variables.
 *
 * Games are dynamic and can be created, removed and manipulated at any
 * stage in the Katan process. New games can be added using the [GameManager].
 *
 * @author Natan Vieira
 * @since  1.0
 */
interface Game : GameVersion {

    /**
     * Returns the settings for that game. [Server]s targeting this game
     * should respect these settings overriding their own.
     */
    val settings: GameSettings

    /**
     * Returns the list of [GameVersion] available for this game.
     */
    val versions: List<GameVersion>

}