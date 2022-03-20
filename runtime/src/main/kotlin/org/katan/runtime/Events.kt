package org.katan.runtime

import kotlinx.coroutines.flow.Flow

interface Events {

    fun listen(event: String, filters: Map<String, String>): Flow<Event>

}