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

package me.devnatan.katan.api.server.composition

import kotlinx.coroutines.CompletableDeferred

/**
 * Represents saved data from a composition, named options.
 */
interface CompositionOptions {

    /**
     * Using this composition option means that the composition is being manufactured directly by the CLI
     * and needs information from those who are executing the creation command, and not synthetically.
     */
    object CLI : CompositionOptions

}

/**
 * A packet is data used for communication between the CLI and the [CompositionFactory].
 */
sealed class CompositionPacket {

    /**
     * Represents the signal to request a value from the CLI.
     */
    class Prompt(
        val text: String,
        val defaultValue: String?,
        val job: CompletableDeferred<String>
    ) : CompositionPacket()

    /**
     * Represents the signal to send a message to the CLI.
     */
    class Message(val text: String, val error: Boolean) : CompositionPacket()

    /**
     * Represents the end of the composition generation cycle.
     */
    object Close : CompositionPacket()

}