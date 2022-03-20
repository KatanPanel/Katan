package org.katan.runtime.internal.event

import kotlinx.coroutines.flow.Flow

interface Events {

    fun listen(event: String, filters: Map<String, String>): Flow<Event>

}