package com.example.debitter.model

import androidx.compose.runtime.Immutable
import java.math.BigDecimal

@Immutable
data class DebitNote(
    val other: List<ChargeLine>,
    val statutory: List<ChargeLine>,
    val advanceReceived: BigDecimal?,
    val company: CompanyBlock,
    val header: NoteHeader,
    val labels: NoteLabels,
) : PrintDocument {
    val hasCharges: Boolean get() = statutory.any { it.isPrintable } || other.any { it.isPrintable }
    val showsAdvance: Boolean get() = advanceReceived != null

    val printableOther: List<ChargeLine> get() = other.filter { it.isPrintable }
    val printableStatutory: List<ChargeLine> get() = statutory.filter { it.isPrintable }

    val subTotal: BigDecimal get() = sum(statutory) + sum(other)
    val total: BigDecimal get() = subTotal - (advanceReceived ?: BigDecimal.ZERO)

    fun lines(section: ChargeSection): List<ChargeLine> = when (section) {
        ChargeSection.OTHER -> other
        ChargeSection.STATUTORY -> statutory
    }

    fun withLines(section: ChargeSection, lines: List<ChargeLine>): DebitNote = when (section) {
        ChargeSection.OTHER -> copy(other = lines)
        ChargeSection.STATUTORY -> copy(statutory = lines)
    }

    private fun sum(lines: List<ChargeLine>): BigDecimal = lines.fold(BigDecimal.ZERO) { running, line -> if (line.isPrintable) running + line.amount!! else running }

    override val kind: DocumentKind get() = DocumentKind.DEBIT_NOTE
}
