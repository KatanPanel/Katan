package org.katan.model.io

import org.katan.KatanException

open class FSException : KatanException()

class BucketNotFoundException(bucket: String) : FSException() {
    override val message: String = bucket
}

class FileNotFoundException : FSException()

class NotAFileException : FSException()

class FileNotAccessibleException : FSException()

class FileNotReadableException : FSException()

class FileNotWritableException : FSException()
