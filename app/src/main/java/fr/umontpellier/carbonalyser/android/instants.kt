package fr.umontpellier.carbonalyser.android

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

val Instant.formatted: String
    get() = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).format(this.atZone(ZoneId.systemDefault()))
