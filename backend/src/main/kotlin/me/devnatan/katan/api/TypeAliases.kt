package me.devnatan.katan.api

typealias EmptySuspendBlock = suspend () -> Unit

typealias SuspendBlock<T> = suspend (T) -> Unit