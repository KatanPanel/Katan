package org.katan.service.unit.instance.http

import org.katan.http.HttpError

internal val UnitInstanceNotFound = HttpError(0, "Instance not found")

internal val UnknownInstanceUpdateStatusCode = HttpError(0, "Unknown update status code")