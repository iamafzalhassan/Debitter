package com.example.debitter.model

import androidx.compose.runtime.Immutable
import java.math.BigDecimal

@Immutable
data class SavedNote(override val createdAt: Long, val billTo: String, override val id: String, val total: BigDecimal, val note: DebitNote) : SavedDocument {
    override val document: PrintDocument get() = note
}
