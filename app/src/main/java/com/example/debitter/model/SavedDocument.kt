package com.example.debitter.model

import java.io.Serializable

sealed interface SavedDocument : Serializable {
    val createdAt: Long

    val id: String

    val document: PrintDocument
}
