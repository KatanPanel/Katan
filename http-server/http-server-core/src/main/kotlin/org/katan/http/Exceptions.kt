package org.katan.http

class KatanHttpException(
    val errorCode: Int,
    val httpStatus: Int,
) : RuntimeException()