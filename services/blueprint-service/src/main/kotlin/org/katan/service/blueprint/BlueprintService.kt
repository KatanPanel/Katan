package org.katan.service.blueprint

import org.katan.model.blueprint.Blueprint
import org.katan.model.blueprint.RawBlueprint

public interface BlueprintService {

    public suspend fun getBlueprint(id: Long): Blueprint?

    public suspend fun downloadBlueprint(source: String): RawBlueprint
}
