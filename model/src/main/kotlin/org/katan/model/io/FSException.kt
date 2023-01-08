package org.katan.model.io

import org.katan.model.KatanException

open class FSException : KatanException()

class BucketNotFoundException : FSException()

class FileNotFoundException : FSException()

class NotAFileException : FSException()

class FileNotAccessibleException : FSException()

class FileNotReadableException : FSException()

class FileNotWritableException : FSException()
