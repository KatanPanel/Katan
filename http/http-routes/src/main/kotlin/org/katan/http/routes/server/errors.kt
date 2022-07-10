package org.katan.http.routes.server

import org.katan.http.HttpError

internal val ServerNotFound = HttpError(1001, "Server not found")

internal val ServerConflict = HttpError(1002, "Server already exists")

internal val ServerMissingCreateOptions = HttpError(1003, "Missing create options")