package org.katan.model.fs

import org.katan.model.KatanException

open class FSException : KatanException()

class BucketReadonlyException : FSException()

class BucketNotFoundException : FSException()

class FileNotFoundException : FSException()

class NotAFileException : FSException()

class FileNotAccessibleException : FSException()