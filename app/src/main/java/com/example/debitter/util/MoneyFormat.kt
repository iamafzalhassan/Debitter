package com.example.debitter.util

import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

object MoneyFormat {
    private const val MAX_DIGITS: Int = 12
    const val SCALE: Int = 2

    private const val GROUP_PATTERN: String = "#,##0"
    private const val PATTERN: String = "#,##0.00"

    val zero: BigDecimal = BigDecimal.ZERO.setScale(SCALE)

    private val amountPattern: Regex = Regex("^[0-9]*\\.?[0-9]*$")

    private val formatter: DecimalFormat = DecimalFormat(PATTERN, DecimalFormatSymbols(Locale.US))
    private val groupFormatter: DecimalFormat = DecimalFormat(GROUP_PATTERN, DecimalFormatSymbols(Locale.US))

    fun format(amount: BigDecimal): String = formatter.format(amount.setScale(SCALE, RoundingMode.HALF_UP))

    fun display(amount: BigDecimal?): String = amount?.let { format(it) }.orEmpty()

    fun grouped(input: String): String? {
        val raw = input.replace(",", "")

        if (raw.isEmpty()) return ""
        if (!amountPattern.matches(raw)) return null

        val dot = raw.indexOf('.')
        val whole = if (dot < 0) raw else raw.substring(0, dot)
        val fraction = if (dot < 0) "" else raw.substring(dot + 1)

        if (fraction.length > SCALE || whole.length > MAX_DIGITS) return null

        val groupedWhole = if (whole.isEmpty()) "" else groupFormatter.format(BigInteger(whole))

        return if (dot < 0) groupedWhole else "$groupedWhole.$fraction"
    }

    fun parse(input: String): BigDecimal? {
        val cleaned = input.filter { it.isDigit() || it == '.' }.trimEnd('.')

        if (cleaned.isEmpty()) return null
        return cleaned.toBigDecimalOrNull()
    }
}
