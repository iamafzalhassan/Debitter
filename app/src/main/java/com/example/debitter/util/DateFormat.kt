package com.example.debitter.util

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateFormat {
    private const val FILE_PATTERN: String = "yyyyMMdd-HHmmss"
    private const val PATTERN: String = "dd-MM-yyyy"

    private val fileFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern(FILE_PATTERN, Locale.US).withZone(ZoneId.systemDefault())
    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern(PATTERN, Locale.US)

    fun format(date: LocalDate?): String = date?.format(formatter).orEmpty()

    fun stamp(instant: Instant): String = fileFormatter.format(instant)
}
