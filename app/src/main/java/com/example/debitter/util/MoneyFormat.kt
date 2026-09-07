package com.example.debitter.util

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

object MoneyFormat {
    private const val MAX_DIGITS: Int = 12

    private const val PATTERN: String = "#,##0.00"

    private val formatter: DecimalFormat = DecimalFormat(PATTERN, DecimalFormatSymbols(Locale.US))

    fun format(amount: BigDecimal): String = formatter.format(amount.setScale(2, RoundingMode.HALF_UP))

    fun parse(input: String): BigDecimal? {
        val cleaned = input.filter { it.isDigit() || it == '.' }.trimEnd('.')

        if (cleaned.isEmpty()) return null
        return cleaned.toBigDecimalOrNull()
    }

    fun sanitize(input: String): String {
        val digitsAndDots = input.filter { it.isDigit() || it == '.' }
        val firstDot = digitsAndDots.indexOf('.')
        val single = if (firstDot < 0) digitsAndDots else digitsAndDots.substring(0, firstDot + 1) + digitsAndDots.substring(firstDot + 1).filter { it != '.' }
        val decimals = if (firstDot < 0) "" else single.substring(firstDot + 1).take(2)
        val whole = (if (firstDot < 0) single else single.substring(0, firstDot)).take(MAX_DIGITS)
        return if (firstDot < 0) whole else "$whole.$decimals"
    }
}
