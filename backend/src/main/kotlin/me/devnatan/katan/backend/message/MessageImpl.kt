package me.devnatan.katan.backend.message

import io.ktor.http.cio.websocket.Frame
import me.devnatan.katan.backend.util.asJsonString

class MessageImpl(
    override val reason: MessageReason = MessageReason.UNKNOWN,
    override val type: MessageType = MessageType.MESSAGE,
    override val content: Map<String, Any>
): Message {

    companion object {

        @JvmStatic
        fun fromMap(map: Map<*, *>): Message? {
            if (!map.containsKey("type") || !map.containsKey("content"))
                return null

            return MessageImpl(
                MessageReason.valueOf((map["reason"] as? String)?.toUpperCase() ?: "UNKNOWN"),
                MessageType.valueOf((map["type"]!! as String).toUpperCase()),
                map["content"]!! as Map<String, Any>
            )
        }

    }

    override val builder: MutableMap<String, Any> = mutableMapOf()

    init {
        append("reason" to reason.name.toLowerCase())
        append("type" to type.name.toLowerCase())
        append("content" to content)
    }

    fun append(pair: Pair<String, Any>): Message {
        builder[pair.first] = pair.second
        return this
    }

}

val Message.frame: Frame.Text
    get() = Frame.Text(builder.asJsonString()!!)