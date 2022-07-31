package org.katan.service.server.http

import org.katan.http.HttpError

internal val UnitNotFound = HttpError(1001, "Unit not found")

internal val UnitConflict = HttpError(1002, "Unit already exists")

internal val UnitMissingCreateOptions =
    HttpError(1003, "Missing unit create options, \"name\" and \"docker-image\" must be set.")

internal val InvalidUnitIdFormat =
    HttpError(1004, "Invalid unit id format")
