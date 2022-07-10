package org.katan.http.routes.server

import org.katan.http.HttpError

internal val UnitNotFound = HttpError(1001, "Server not found")

internal val UnitConflict = HttpError(1002, "Server already exists")

internal val UnitMissingCreateOptions = HttpError(1003, "Missing create options")