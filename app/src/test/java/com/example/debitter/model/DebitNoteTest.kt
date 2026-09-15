package com.example.debitter.model

import com.example.debitter.data.Defaults
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

class DebitNoteTest {
    private val blank: DebitNote = Defaults.note(LocalDate.of(2026, 9, 15))

    private val charged: DebitNote = blank
        .withLines(ChargeSection.STATUTORY, listOf(line("CUSTOMS DUTY", "150000.00"), line("", "999.00"), line("VAT", "0.00")))
        .withLines(ChargeSection.OTHER, listOf(line("TRANSPORT", "25000.50"), line("DELIVERY ORDER", null)))

    @Test
    fun blankNoteHasNoCharges() {
        assertFalse(blank.hasCharges)
        assertEquals(0, blank.subTotal.signum())
    }

    @Test
    fun subTotalSumsOnlyPrintableRows() {
        assertTrue(charged.hasCharges)
        assertEquals(listOf("CUSTOMS DUTY"), charged.printableStatutory.map { it.label })
        assertEquals(listOf("TRANSPORT"), charged.printableOther.map { it.label })
        assertEquals(BigDecimal("175000.50"), charged.subTotal)
    }

    @Test
    fun totalSubtractsTheAdvance() {
        val withAdvance = charged.copy(advanceReceived = BigDecimal("50000.00"))

        assertTrue(withAdvance.showsAdvance)
        assertEquals(BigDecimal("125000.50"), withAdvance.total)
        assertFalse(charged.showsAdvance)
        assertEquals(BigDecimal("175000.50"), charged.total)
    }

    private fun line(label: String, amount: String?): ChargeLine = ChargeLine(appendsSuffix = false, label = label, amount = amount?.let(::BigDecimal), id = UUID.randomUUID())
}
