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

import br.com.devsrsouza.eventkt.EventScope

/**
 * An event that can be launched throughout the life cycle and a closed scope (plugin for example)
 * or for the entire application, events are published using a [EventScope].
 *
 * @see RemoteEvent
 * @see EventSource
 * @author Natan Vieira
 */
interface Event

/**
 * Events can be launched from any location until they reach the application's internal context,
 * we need to know their origin in order to handle them properly and appropriately.
 *
 * @see Event
 * @see EventSource
 * @author Natan Vieira
 */
interface RemoteEvent : Event {

    /**
     * Returns the event source.
     */
    val source: EventSource

}