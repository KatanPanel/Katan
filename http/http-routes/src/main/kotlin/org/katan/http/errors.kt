package org.katan.http

internal val UnitNotFound = HttpError(1001, "Unit not found")

internal val UnitConflict = HttpError(1002, "Unit already exists")

internal val UnitMissingCreateOptions = HttpError(1003, "Missing unit create options")