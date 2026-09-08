package com.example.debitter.model

import androidx.compose.runtime.Immutable
import java.io.Serializable

@Immutable
data class NoteLabels(
    val advanceReceived: String,
    val billTo: String,
    val blAwbNo: String,
    val chargeSuffix: String,
    val consignment: String,
    val containerNo: String,
    val customsEntry: String,
    val date: String,
    val otherSection: String,
    val signature: String,
    val statutorySection: String,
    val subTotal: String,
    val title: String,
    val total: String,
    val vesselFlight: String,
    val voyageNoDate: String,
) : Serializable
