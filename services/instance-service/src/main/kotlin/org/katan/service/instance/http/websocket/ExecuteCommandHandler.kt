package org.katan.service.instance.http.websocket

import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import org.katan.http.websocket.WebSocketPacket.Companion.TARGET_ID
import org.katan.http.websocket.WebSocketPacket.Companion.VALUE
import org.katan.http.websocket.WebSocketPacketContext
import org.katan.http.websocket.WebSocketPacketEventHandler
import org.katan.http.websocket.stringData
import org.katan.model.toSnowflake
import org.katan.service.instance.InstanceService
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class ExecuteCommandHandler :
    WebSocketPacketEventHandler(), KoinComponent {

    private val instanceService by inject<InstanceService>()

    override suspend fun WebSocketPacketContext.handle() {
        val target = stringData(TARGET_ID)?.toLongOrNull()?.toSnowflake() ?: return
        val input = stringData(VALUE) ?: return

        launch(IO) {
            instanceService.runInstanceCommand(target, input)
        }
    }
}
