package org.katan.event.di

import org.katan.event.EventScope
import org.katan.event.EventScopeImpl
import org.koin.core.module.Module
import org.koin.dsl.module

val eventsDispatcherDI: Module = module {
    single<EventScope> { EventScopeImpl() }
}
