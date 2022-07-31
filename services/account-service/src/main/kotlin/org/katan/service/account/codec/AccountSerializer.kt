package org.katan.service.account.codec

import kotlinx.datetime.Instant
import kotlinx.datetime.serializers.InstantIso8601Serializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure
import org.katan.model.account.Account
import org.katan.service.account.AccountImpl

@Serializer(forClass = Account::class)
public object AccountSerializer : KSerializer<Account> {

    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("org.katan.model.account") {
            element<Long>("id")
            element<String>("username")
            element<Instant>("created_at")
            element<Instant>("updated_at")
            element<Instant?>("last_logged_in_at", isOptional = true)
        }

    override fun serialize(encoder: Encoder, value: Account) {
        encoder.encodeStructure(descriptor) {
            encodeLongElement(descriptor, 0, value.id)
            encodeStringElement(descriptor, 1, value.username)
            encodeSerializableElement(descriptor, 2, InstantIso8601Serializer, value.createdAt)
            encodeSerializableElement(descriptor, 3, InstantIso8601Serializer, value.updatedAt)
            encodeNullableSerializableElement(
                descriptor,
                4,
                InstantIso8601Serializer,
                value.lastLoggedInAt
            )
        }
    }

    override fun deserialize(decoder: Decoder): Account {
        return decoder.decodeStructure(descriptor) {
            AccountImpl(
                id = decodeLongElement(descriptor, 0),
                username = decodeStringElement(descriptor, 1),
                createdAt = decodeSerializableElement(descriptor, 2, InstantIso8601Serializer),
                updatedAt = decodeSerializableElement(descriptor, 3, InstantIso8601Serializer),
                lastLoggedInAt = decodeNullableSerializableElement(
                    descriptor,
                    4,
                    InstantIso8601Serializer.nullable
                ),
            )
        }
    }

}