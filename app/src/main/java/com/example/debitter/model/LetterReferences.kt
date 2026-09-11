package com.example.debitter.model

import androidx.compose.runtime.Immutable
import java.io.Serializable

@Immutable
data class LetterReferences(val blNo: String, val containerNo: String, val receiptNo: String, val vessel: String, val voyage: String) : Serializable
