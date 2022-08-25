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
import org.katan.service.unit.instance.http.routes.getInstanceFile
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
                getInstanceFile()
            }

//            webSocket {
//                logger.info("onConnect")
//                try {
//                    for (frame in incoming) {
//                        logger.info("connected")
//                        frame as? Frame.Text ?: continue
//                        val receivedText = frame.readText()
//                        logger.info("received: $receivedText")
//
//                        try {
//                            val packet =
//                                json.decodeFromString<Packet>(receivedText)
//
//                            logger.info("decoded: $packet")
//                            launch(Default) {
//                                when (packet.op) {
//                                    0 -> {
//                                        val targetId =
//                                            packet.data["tid"] ?: return@launch
//
//                                        logger.info("tid: $targetId")
//                                        instanceService.fetchInstanceLogs(targetId.toLong())
//                                            .collect { frame ->
//                                                logger.info("collected: $frame")
//                                                send(
//                                                    Frame.Text(
//                                                        json.encodeToString(
//                                                            Packet(
//                                                                packet.op,
//                                                                mapOf(
//                                                                    "v" to frame
//                                                                )
//                                                            )
//                                                        )
//                                                    )
//                                                )
//                                            }
//                                    }
//
//                                    1 -> {
//                                        val targetId = packet.data["tid"]?.ifBlank { null } ?.toLong() ?: return@launch
//                                        val input = packet.data["v"]?.ifBlank { null }
//                                            ?: return@launch
//
//                                        logger.info("tid: $targetId")
//                                        logger.info("input: $input")
//                                        instanceService.executeInstanceCommand(targetId, input)
//                                            .collect { frame ->
//                                                logger.info("frame collected from input response: $frame")
//                                            }
//                                    }
//
//                                    else -> Unit
//                                }
//                            }
//                        } catch (e: Throwable) {
//                            logger.error("received but error occurred", e)
//                        }
//                    }
//                } catch (e: ClosedReceiveChannelException) {
//                    logger.error("ws closed ${closeReason.await()}", e)
//                } catch (e: Throwable) {
//                    logger.error("ws error ${closeReason.await()}", e)
//                }
        }
    }
}
