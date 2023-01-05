package org.katan.service.unit.http

import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.routing
import org.katan.http.HttpModule
import org.katan.service.unit.http.routes.createUnit
import org.katan.service.unit.http.routes.getUnit
import org.katan.service.unit.http.routes.getUnitAuditLogs
import org.katan.service.unit.http.routes.listUnits
import org.katan.service.unit.http.routes.modifyUnit

internal class UnitHttpModule : HttpModule() {

    override fun install(app: Application) {
        app.routing {
            authenticate {
                listUnits()
                getUnit()
                modifyUnit()
                createUnit()
                getUnitAuditLogs()
            }
        }
    }
}
