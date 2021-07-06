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
 * Represents a server's container.
 * @property id the container identification.
 */
abstract class ServerContainer(
    val id: String,
    val name: String
) {

    /**
     * Returns the result of the server inspection.
     */
    var inspection: ServerContainerInspection =
        ServerContainerInspection.NotInspected

    /**
     * Returns if the container has already been inspected.
     */
    open fun isInspected(): Boolean {
        return synchronized(inspection) {
            inspection !is ServerContainerInspection.NotInspected
        }
    }

    /**
     * Starts this [ServerContainer] and suspend this function until the container has been started.
     */
    abstract suspend fun start()

    /**
     * Stops this [ServerContainer] and suspend this function until the container has been stopped.
     */
    abstract suspend fun stop()

    override fun toString(): String {
        return "$name ($id)"
    }

}