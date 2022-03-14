@file:JvmName("Application")

package org.katan

import org.katan.maestro.di.MaestroModule
import org.koin.core.context.startKoin

fun main() {
	startKoin {
		modules(MaestroModule)
	}
}