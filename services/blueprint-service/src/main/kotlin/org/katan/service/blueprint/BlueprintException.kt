package org.katan.service.blueprint

import org.katan.KatanException

public open class BlueprintException internal constructor() : KatanException()

public class BlueprintNotFoundException internal constructor() : BlueprintException()

public class NoMatchingBlueprintSpecProviderException internal constructor() : BlueprintException()

public class BlueprintSpecNotFound internal constructor() : BlueprintException()

public class BlueprintConflictException internal constructor() : BlueprintException()

public class UnsupportedBlueprintSpecSource internal constructor() : BlueprintException()
