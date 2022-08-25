package org.katan.model.fs

import org.katan.model.KatanException

open class FileSystemException : KatanException()

class BucketReadonlyException : FileSystemException()

class BucketNotFoundException : FileSystemException()

class FileNotFoundException : FileSystemException()
