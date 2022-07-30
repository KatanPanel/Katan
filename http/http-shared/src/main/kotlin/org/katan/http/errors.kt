package org.katan.http

internal val UnitNotFound = HttpError(1001, "Unit not found")

internal val UnitConflict = HttpError(1002, "Unit already exists")

internal val UnitMissingCreateOptions =
    HttpError(1003, "Missing unit create options, \"name\" and \"docker-image\" must be set.")

internal val InvalidUnitIdFormat =
    HttpError(1004, "Invalid unit id format")

internal val UnitInstanceNotFound = HttpError(2001, "Unit instance not found")

internal val ErrorWhileFetchingUnitInstance =
    HttpError(2002, "Couldn't fetch unit instance info, try again later.")

internal val AccountNotFound = HttpError(3001, "Account not found")