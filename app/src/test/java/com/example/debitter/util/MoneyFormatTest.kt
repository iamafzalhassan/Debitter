package com.example.debitter.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.math.BigDecimal
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class MoneyFormatTest {
    @Test
    fun formatConcurrentlyGivesTheSameResultOnEveryThread() {
        val executor = Executors.newFixedThreadPool(8)
        val results = executor.invokeAll(List(8) { Callable { List(500) { MoneyFormat.format(BigDecimal("98765432.105")) }.toSet() } }).flatMap { it.get() }.toSet()
        executor.shutdown()
        executor.awaitTermination(5, TimeUnit.SECONDS)

        assertEquals(setOf("98,765,432.11"), results)
    }

    @Test
    fun formatGroupsThousandsWithTwoDecimals() {
        assertEquals("1,234,567.50", MoneyFormat.format(BigDecimal("1234567.5")))
        assertEquals("0.00", MoneyFormat.format(BigDecimal.ZERO))
    }

    @Test
    fun formatRoundsHalfUp() {
        assertEquals("0.01", MoneyFormat.format(BigDecimal("0.005")))
        assertEquals("10.00", MoneyFormat.format(BigDecimal("9.995")))
    }

    @Test
    fun parseIgnoresGroupingAndRejectsEmptyInput() {
        assertEquals(BigDecimal("1250.00"), MoneyFormat.parse("1,250.00"))
        assertNull(MoneyFormat.parse(""))
        assertNull(MoneyFormat.parse(","))
    }

    @Test
    fun sanitizeKeepsOneDecimalPointAndTwoFractionDigits() {
        assertEquals("12345.67", MoneyFormat.sanitize("12,345.678"))
        assertEquals("1.23", MoneyFormat.sanitize("1.2.3"))
        assertEquals("123456789012", MoneyFormat.sanitize("1234567890123"))
    }
}
