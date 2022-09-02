package org.katan.service.instance

import org.katan.model.KatanException

open class InstanceException : KatanException()

class InstanceNotFoundException : InstanceException()

class InstanceNotAvailableException : InstanceException()
