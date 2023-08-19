package org.katan.model.io

import org.katan.model.KatanException

public open class FSException : KatanException()

public class BucketNotFoundException(bucket: String) : FSException() {
    override val message: String = bucket
}

public class FileNotFoundException : FSException()

public class NotAFileException : FSException()

public class FileNotAccessibleException : FSException()

public class FileNotReadableException : FSException()

public class FileNotWritableException : FSException()
