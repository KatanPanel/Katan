package me.devnatan.katan.backend.message

interface Message {

    val builder: MutableMap<String, Any>

    val type: MessageType

    val reason: MessageReason

    val content: Map<String, Any>

    operator fun contains(key: String) = content.containsKey(key)

    operator fun get(key: String) = content[key]

}

enum class MessageReason {

    SERVER_UPDATED,

    SERVER_ALREADY_STARTED,

    UNKNOWN

}

enum class MessageType {

    COMMAND,

    MESSAGE,

    SERVER_LOG

}