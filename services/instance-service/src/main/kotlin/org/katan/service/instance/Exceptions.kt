package org.katan.service.instance

import org.katan.model.KatanException

public open class InstanceException : KatanException()

public class InstanceNotFoundException : InstanceException()

public class InstanceNotAvailableException : InstanceException()
