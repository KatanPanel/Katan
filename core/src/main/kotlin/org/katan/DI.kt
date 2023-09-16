package org.katan

import org.koin.core.module.Module
import org.koin.dsl.module

val coreDI: Module = module {
    single<EventsDispatcher> { EventsDispatcherImpl() }
    single { KatanConfig() }
}
