package me.devnatan.katan.frontend

import kotlin.js.Date

typealias EmptyBlock = (() -> Unit)
typealias DynamicBlock = ((dynamic) -> Unit)

fun now(): Long = Date.now().toLong()

fun measureTimeMillis(block: () -> Unit): Long {
    val a = now()
    block()
    return now() - a
}