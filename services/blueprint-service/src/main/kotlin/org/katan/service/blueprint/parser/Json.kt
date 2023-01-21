package org.katan.service.blueprint.parser

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

internal fun JsonObject.string(key: String) = getValue(key).jsonPrimitive.content

internal fun JsonObject.struct(key: String) = this[key]?.jsonObject
