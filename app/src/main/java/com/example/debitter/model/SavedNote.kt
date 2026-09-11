package com.example.debitter.model

import androidx.compose.runtime.Immutable
import java.io.Serializable
import java.math.BigDecimal

@Immutable
data class SavedNote(val createdAt: Long, val billTo: String, val id: String, val total: BigDecimal, val note: DebitNote) : Serializable
