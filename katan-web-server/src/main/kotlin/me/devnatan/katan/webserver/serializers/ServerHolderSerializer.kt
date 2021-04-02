package me.devnatan.katan.webserver.serializers

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import me.devnatan.katan.api.server.ServerHolder

class ServerHolderSerializer : JsonSerializer<ServerHolder>() {

    override fun serialize(
        value: ServerHolder, gen: JsonGenerator,
        serializers:
        SerializerProvider
    ) {
        gen.writeStartObject()
        // skip server since its is the referrer, we already have it
        gen.writeObjectField("account", value.account)
        gen.writeEndObject()
    }

}