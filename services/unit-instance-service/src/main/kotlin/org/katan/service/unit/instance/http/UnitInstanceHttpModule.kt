package org.katan.service.unit.instance.http

import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.routing
import org.katan.http.di.HttpModule
import org.katan.http.di.HttpModuleRegistry
import org.katan.http.websocket.WebSocketOp
import org.katan.http.websocket.WebSocketOpCodes.EXECUTE_INSTANCE_COMMAND
import org.katan.http.websocket.WebSocketOpCodes.FETCH_INSTANCE_LOGS
import org.katan.http.websocket.WebSocketPacketEventHandler
import org.katan.service.unit.instance.http.routes.getInstance
import org.katan.service.unit.instance.http.routes.getInstanceFsBucket
import org.katan.service.unit.instance.http.routes.getInstanceFsFile
import org.katan.service.unit.instance.http.routes.updateStatus
import org.katan.service.unit.instance.http.websocket.ExecuteCommandHandler
import org.katan.service.unit.instance.http.websocket.FetchLogsHandler

internal class UnitInstanceHttpModule(
    registry: HttpModuleRegistry
) : HttpModule(registry) {

    override fun webSocketHandlers(): Map<WebSocketOp, WebSocketPacketEventHandler> {
        return mapOf(
            FETCH_INSTANCE_LOGS to FetchLogsHandler(),
            EXECUTE_INSTANCE_COMMAND to ExecuteCommandHandler()
        )
    }

    override fun install(app: Application) {
        app.routing {
            authenticate {
                getInstance()
                updateStatus()
                getInstanceFsFile()
                getInstanceFsBucket()
            }
        }
    }
}
