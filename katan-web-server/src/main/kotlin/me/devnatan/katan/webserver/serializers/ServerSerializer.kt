package me.devnatan.katan.webserver.serializers

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import me.devnatan.katan.api.server.Server

class ServerSerializer : JsonSerializer<Server>() {

    override fun serialize(
        value: Server,
        gen: JsonGenerator,
        serializers: SerializerProvider
    ) {
        gen.writeStartObject()
        gen.writeNumberField("id", value.id)
        gen.writeStringField("name", value.name)
        gen.writeStringField("state", value.state.name.toLowerCase())
        gen.writeObjectField("game", value.game)
        gen.writeStringField("host", value.host)
        gen.writeNumberField("port", value.port)
        gen.writeObjectField("holders", value.holders)

        gen.writeObjectFieldStart("container")
        gen.writeStringField("id", value.container.id)
        gen.writeBooleanField("is_inspected", value.container.isInspected())
        gen.writeObjectField("inspection", value.container.inspection)
        gen.writeEndObject()
        gen.writeEndObject()
    }

}