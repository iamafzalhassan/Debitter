package com.example.debitter.model

import java.io.Serializable
import java.math.BigDecimal

data class SavedNote(val createdAt: Long, val billTo: String, val id: String, val note: DebitNote, val total: BigDecimal) : Serializable
