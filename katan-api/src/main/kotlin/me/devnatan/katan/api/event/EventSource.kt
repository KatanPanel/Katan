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

package me.devnatan.katan.api.event

import me.devnatan.katan.api.security.account.Account

/**
 * The source from which an [Event] comes.
 *
 * @see Event
 * @see RemoteEvent
 * @author Natan Vieira
 */
sealed class EventSource {

    /**
     * The event originated from the application itself.
     */
    object Internal : EventSource()

    /**
     * The event was originated from the web user interface.
     *
     * @property account the account responsible for originating the event
     */
    data class WebUI(val account: Account) : EventSource()

    /**
     * The event was originated from the command line interface.
     */
    object CLI : EventSource()

    /**
     * The event originated from an external entity.
     */
    object External : EventSource()

    /**
     * Unknown origin.
     */
    object Unknown : EventSource()

}