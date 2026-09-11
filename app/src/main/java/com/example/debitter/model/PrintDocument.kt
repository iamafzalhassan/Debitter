package com.example.debitter.model

import java.io.Serializable

sealed interface PrintDocument : Serializable {
    val kind: DocumentKind
}
