package org.katan.http.routes

import org.katan.http.routes.unit.di.UnitRoutesHttpDI
import org.koin.dsl.module

val HttpRoutesDI: org.koin.core.module.Module = module {
    includes(UnitRoutesHttpDI)
}