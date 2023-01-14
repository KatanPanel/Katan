package org.katan.service.blueprint

import org.katan.model.KatanException

open class BlueprintException : KatanException()

class BlueprintNotFoundException : BlueprintException()

class NoMatchingBlueprintSpecProviderException : BlueprintException()

class BlueprintSpecNotFound : BlueprintException()

class BlueprintConflictException : BlueprintException()

class BlueprintSpecParseException(
    override val message: String,
    override val cause: Throwable?
) : BlueprintException()

class UnsupportedBlueprintSpecSource : BlueprintException()
