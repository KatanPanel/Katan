package org.katan.model.io

import org.katan.model.KatanException

public open class NetworkException(message: String?, cause: Throwable? = null) : KatanException(message, cause)

public class InvalidNetworkAssignmentException(message: String) : NetworkException(message)

public class UnknownNetworkException(public val network: String) : NetworkException(network)

public class NetworkConnectionFailed(public val network: String, cause: Throwable? = null) :
    NetworkException(network, cause)
