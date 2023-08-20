package org.katan.model.instance

import org.katan.model.KatanException

public open class InstanceException : KatanException()

public class InstanceNotFoundException : InstanceException()

public class InstanceUnreachableRuntimeException : InstanceException()
