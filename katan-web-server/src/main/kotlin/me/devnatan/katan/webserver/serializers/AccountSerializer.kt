package me.devnatan.katan.webserver.serializers

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import me.devnatan.katan.api.security.account.Account
import me.devnatan.katan.common.impl.account.SecureAccount

class AccountSerializer : JsonSerializer<Account>() {

    override fun serialize(
        value: Account,
        gen: JsonGenerator,
        serializers: SerializerProvider
    ) {
        gen.writeStartObject()
        gen.writeStringField("id", value.id.toString());
        gen.writeStringField("username", value.username)
        gen.writeObjectField("registered_at", value.registeredAt)
        gen.writeObjectField("last_login", value.lastLogin)
        gen.writeObjectField("role", value.role)

        if (value is SecureAccount)
            gen.writeOmittedField("password")
        gen.writeEndObject()
    }

}