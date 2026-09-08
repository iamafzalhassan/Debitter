package com.example.debitter.util

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

object MoneyFormat {
    const val SEPARATOR: Char = ','

    const val GROUP_SIZE: Int = 3
    private const val MAX_DIGITS: Int = 12
    const val SCALE: Int = 2

    private const val PATTERN: String = "#,##0.00"

    val zero: BigDecimal = BigDecimal.ZERO.setScale(SCALE)

    private val formatter: DecimalFormat = DecimalFormat(PATTERN, DecimalFormatSymbols(Locale.US))

    fun format(amount: BigDecimal): String = formatter.format(amount.setScale(SCALE, RoundingMode.HALF_UP))

    fun plain(amount: BigDecimal?): String = amount?.setScale(SCALE, RoundingMode.HALF_UP)?.toPlainString().orEmpty()

    fun sanitize(input: String): String {
        val cleaned = input.filter { it.isDigit() || it == '.' }
        val dot = cleaned.indexOf('.')

        if (dot < 0) return cleaned.take(MAX_DIGITS)

        val whole = cleaned.substring(0, dot).take(MAX_DIGITS)
        val fraction = cleaned.substring(dot + 1).filter { it != '.' }.take(SCALE)

        return "$whole.$fraction"
    }

    fun parse(input: String): BigDecimal? {
        val cleaned = input.filter { it.isDigit() || it == '.' }.trimEnd('.')

        if (cleaned.isEmpty()) return null
        return cleaned.toBigDecimalOrNull()
    }
}
