package org.katan.service.instance.di

import org.katan.http.importHttpModule
import org.katan.service.instance.http.UnitInstanceHttpModule
import org.koin.core.module.Module
import org.koin.dsl.module

val unitInstanceServiceDI: Module = module {
    importHttpModule(::UnitInstanceHttpModule)
}
