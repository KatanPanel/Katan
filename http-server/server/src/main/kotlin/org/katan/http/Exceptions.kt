package org.katan.http

class Exceptions(
    val errorCode: Int,
    val httpStatus: Int,
) : RuntimeException()