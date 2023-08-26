package org.katan.model.project

import org.katan.model.KatanException

public open class ProjectException : KatanException()

public class ProjectNotFoundException : ProjectException()
