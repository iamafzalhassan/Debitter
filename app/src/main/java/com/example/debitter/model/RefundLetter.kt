package com.example.debitter.model

import androidx.compose.runtime.Immutable
import java.time.LocalDate

@Immutable
data class RefundLetter(
    val letterhead: Letterhead,
    val labels: LetterLabels,
    val references: LetterReferences,
    val date: LocalDate?,
    val agent: ShippingAgent,
) : PrintDocument {
    val isReady: Boolean get() = letterhead.name.isNotBlank() && agent.name.isNotBlank() && references.containerNo.isNotBlank()

    override val kind: DocumentKind get() = DocumentKind.REFUND_LETTER
}
