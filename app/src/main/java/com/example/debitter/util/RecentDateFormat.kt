package com.example.debitter.util

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object RecentDateFormat {
    private const val PATTERN: String = "d MMM, h:mm a"

    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern(PATTERN, Locale.US).withZone(ZoneId.systemDefault())

    fun format(epochMillis: Long): String = formatter.format(Instant.ofEpochMilli(epochMillis))
}
