package com.example.debitter.model

import java.io.Serializable
import java.math.BigDecimal

data class DebitNote(
    val other: List<ChargeLine>,
    val statutory: List<ChargeLine>,
    val advanceReceived: BigDecimal?,
    val company: CompanyBlock,
    val header: NoteHeader,
    val labels: NoteLabels,
) : Serializable {
    val printableOther: List<ChargeLine> get() = other.filter { it.isPrintable }

    val printableStatutory: List<ChargeLine> get() = statutory.filter { it.isPrintable }

    val hasCharges: Boolean get() = printableStatutory.isNotEmpty() || printableOther.isNotEmpty()

    val showsAdvance: Boolean get() = advanceReceived != null

    val subTotal: BigDecimal get() = (printableStatutory + printableOther).fold(BigDecimal.ZERO) { sum, line -> sum + line.amount!! }

    val total: BigDecimal get() = subTotal - (advanceReceived ?: BigDecimal.ZERO)

    fun lines(section: ChargeSection): List<ChargeLine> = when (section) {
        ChargeSection.OTHER -> other
        ChargeSection.STATUTORY -> statutory
    }

    fun withLines(section: ChargeSection, lines: List<ChargeLine>): DebitNote = when (section) {
        ChargeSection.OTHER -> copy(other = lines)
        ChargeSection.STATUTORY -> copy(statutory = lines)
    }
}
