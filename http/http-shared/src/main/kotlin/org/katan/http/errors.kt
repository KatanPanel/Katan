package org.katan.http

val UnitNotFound = HttpError(1001, "Unit not found")

val UnitConflict = HttpError(1002, "Unit already exists")

val UnitMissingCreateOptions =
    HttpError(1003, "Missing unit create options, \"name\" and \"docker-image\" must be set.")

val InvalidUnitIdFormat =
    HttpError(1004, "Invalid unit id format")

val UnitInstanceNotFound = HttpError(2001, "Unit instance not found")

val AccountNotFound = HttpError(3001, "Account not found")