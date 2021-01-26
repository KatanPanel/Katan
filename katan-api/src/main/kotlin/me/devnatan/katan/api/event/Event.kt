package me.devnatan.katan.api.event

import br.com.devsrsouza.eventkt.EventScope

/**
 * It represents an event that can be launched throughout the life cycle and a closed scope (plugin for example)
 * or for the entire application, events are published using a [EventScope].
 */
interface Event