package com.example.debitter.model

import java.io.Serializable
import java.math.BigDecimal
import java.util.UUID

data class ChargeLine(val label: String, val amount: BigDecimal?, val id: UUID) : Serializable {
    companion object {
        fun empty(label: String = ""): ChargeLine = ChargeLine(label = label, amount = null, id = UUID.randomUUID())
    }

    val isPrintable: Boolean get() = amount != null && label.isNotBlank()
}
