package com.example.debitter.model

import com.example.debitter.util.MoneyFormat
import java.io.Serializable
import java.math.BigDecimal
import java.util.UUID

data class ChargeLine(val label: String, val amount: BigDecimal?, val id: UUID) : Serializable {
    companion object {
        fun empty(label: String = ""): ChargeLine = ChargeLine(label = label, amount = MoneyFormat.zero, id = UUID.randomUUID())
    }

    val isPrintable: Boolean get() = amount != null && amount.signum() != 0 && label.isNotBlank()
}
