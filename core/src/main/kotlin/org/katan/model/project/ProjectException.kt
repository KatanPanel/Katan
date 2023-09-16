package org.katan.model.project

import org.katan.KatanException

open class ProjectException : KatanException()

class ProjectNotFoundException : ProjectException()
