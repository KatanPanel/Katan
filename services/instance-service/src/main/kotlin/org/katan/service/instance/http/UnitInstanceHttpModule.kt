package org.katan.service.instance.http

import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.routing
import org.katan.http.di.HttpModule
import org.katan.http.di.HttpModuleRegistry
import org.katan.http.websocket.WebSocketOp
import org.katan.http.websocket.WebSocketOpCodes.INSTANCE_RUN_COMMAND
import org.katan.http.websocket.WebSocketOpCodes.INSTANCE_FETCH_LOGS
import org.katan.http.websocket.WebSocketOpCodes.INSTANCE_STATS_STREAMING
import org.katan.http.websocket.WebSocketPacketEventHandler
import org.katan.service.instance.http.routes.getInstance
import org.katan.service.instance.http.routes.getInstanceFsBucket
import org.katan.service.instance.http.routes.getInstanceFsFile
import org.katan.service.instance.http.routes.readFsFile
import org.katan.service.instance.http.routes.updateStatus
import org.katan.service.instance.http.websocket.ExecuteCommandHandler
import org.katan.service.instance.http.websocket.FetchLogsHandler
import org.katan.service.instance.http.websocket.StatsStreamingHandler

internal class UnitInstanceHttpModule(
    registry: HttpModuleRegistry
) : HttpModule(registry) {

    override fun webSocketHandlers(): Map<WebSocketOp, WebSocketPacketEventHandler> {
        return mapOf(
            INSTANCE_FETCH_LOGS to FetchLogsHandler(),
            INSTANCE_RUN_COMMAND to ExecuteCommandHandler(),
            INSTANCE_STATS_STREAMING to StatsStreamingHandler()
        )
    }

    override fun install(app: Application) {
        app.routing {
            authenticate {
                getInstance()
                updateStatus()
                getInstanceFsFile()
                getInstanceFsBucket()
                readFsFile()
            }
        }
    }
}
