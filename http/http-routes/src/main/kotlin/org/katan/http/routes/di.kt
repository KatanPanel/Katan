package org.katan.http.routes

import org.katan.http.routes.unit.di.unitHttpRoutesDI
import org.katan.http.routes.unit_instance.di.unitInstanceHttpRoutesDI
import org.koin.dsl.module

val httpRoutesDI: org.koin.core.module.Module = module {
    includes(unitHttpRoutesDI, unitInstanceHttpRoutesDI)
}