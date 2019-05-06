package me.devnatan.katan.api

import java.io.Writer

fun Writer.writeln(line: String) = write(line + '\n')