package org.katan.event

import org.koin.core.module.Module
import org.koin.dsl.module

public val eventsDispatcherDI: Module = module {
    single<EventsDispatcher> { EventsDispatcherImpl() }
}