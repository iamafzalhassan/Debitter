package com.example.debitter.model

import java.io.Serializable
import java.time.LocalDate

data class NoteHeader(
    val billTo: String,
    val blAwbNo: String,
    val consignment: String,
    val containerNo: String,
    val customsEntry: String,
    val vesselFlight: String,
    val voyageNoDate: String,
    val date: LocalDate?,
) : Serializable
