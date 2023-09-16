package org.katan.service.unit

import org.katan.KatanException

public open class UnitException : KatanException()

public class UnitConflictException : UnitException()

public class UnitNotFoundException : UnitException()
