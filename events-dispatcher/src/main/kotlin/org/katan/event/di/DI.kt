package org.katan.event.di

import org.katan.event.EventsDispatcher
import org.katan.event.EventsDispatcherImpl
import org.koin.core.module.Module
import org.koin.dsl.module

val eventsDispatcherDI: Module = module {
    single<EventsDispatcher> { EventsDispatcherImpl() }
}
