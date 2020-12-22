package me.devnatan.katan.common.util

import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

val currentZoneId: ZoneId by lazy {
    TimeZone.getTimeZone(System.getProperty("katan.timezone", TimeZone.getDefault().id)).toZoneId()
}

val dateTimeFormatter: DateTimeFormatter by lazy {
    DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL).withZone(currentZoneId)
}

val dateFormatter: DateTimeFormatter by lazy {
    DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).withZone(currentZoneId)
}

val timeFormatter: DateTimeFormatter by lazy {
    DateTimeFormatter.ofLocalizedTime(FormatStyle.FULL).withZone(currentZoneId)
}