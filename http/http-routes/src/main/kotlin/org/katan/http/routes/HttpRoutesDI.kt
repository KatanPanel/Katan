package org.katan.http.routes

import org.katan.http.routes.unitInstance.di.unitInstanceHttpRoutesDI
import org.koin.dsl.module

val httpRoutesDI: org.koin.core.module.Module = module {
    includes(unitInstanceHttpRoutesDI)
}
