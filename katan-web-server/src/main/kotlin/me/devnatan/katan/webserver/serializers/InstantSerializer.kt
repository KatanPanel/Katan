package me.devnatan.katan.webserver.serializers

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import io.ktor.http.*
import java.time.Instant

class InstantSerializer : JsonSerializer<Instant>() {

    override fun serialize(value: Instant, gen: JsonGenerator, serializers: SerializerProvider) {
        gen.writeString(value.toHttpDateString())
    }

}