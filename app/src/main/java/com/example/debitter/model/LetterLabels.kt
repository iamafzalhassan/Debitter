package com.example.debitter.model

import androidx.compose.runtime.Immutable
import java.io.Serializable

@Immutable
data class LetterLabels(
    val attention: String,
    val blNo: String,
    val body: String,
    val closing: String,
    val containerNo: String,
    val emphasis: String,
    val receiptNo: String,
    val salutation: String,
    val signatoryName: String,
    val signatoryPhone: String,
    val signatoryTitle: String,
    val signOff: String,
    val title: String,
    val vessel: String,
    val voyage: String,
) : Serializable
