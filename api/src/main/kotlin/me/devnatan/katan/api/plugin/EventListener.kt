package me.devnatan.katan.api.plugin

import br.com.devsrsouza.eventkt.EventScope
import br.com.devsrsouza.eventkt.scopes.LocalEventScope
import br.com.devsrsouza.eventkt.scopes.SimpleEventScope
import br.com.devsrsouza.eventkt.scopes.asSimple
import br.com.devsrsouza.eventkt.scopes.listen
import kotlinx.coroutines.CoroutineScope

/**
 * Responsible for sending events from the plugin and receiving events for the plugin, also known as EventBus.
 */
open class EventListener(val coroutineScope: CoroutineScope) : EventScope by LocalEventScope().asSimple() {

    @Suppress("CAST_NEVER_SUCCEEDS")
    inline fun <reified T : Any> event(noinline block: suspend T.() -> Unit) {
        (this as SimpleEventScope).listen(this, coroutineScope, block)
    }

}