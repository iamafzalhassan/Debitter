package com.example.debitter.model

import androidx.compose.runtime.Immutable

@Immutable
data class SavedLetter(override val createdAt: Long, val agent: String, val customer: String, override val id: String, val letter: RefundLetter) : SavedDocument {
    override val document: PrintDocument get() = letter
}
