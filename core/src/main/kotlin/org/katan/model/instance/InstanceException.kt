package org.katan.model.instance

import org.katan.KatanException

open class InstanceException : KatanException()

class InstanceNotFoundException : InstanceException()

class InstanceUnreachableRuntimeException : InstanceException()
