package com.example.debitter.model

import com.example.debitter.util.MoneyFormat
import java.io.Serializable
import java.math.BigDecimal
import java.util.UUID

data class ChargeLine(val appendsSuffix: Boolean, val label: String, val amount: BigDecimal?, val id: UUID) : Serializable {
    companion object {
        fun preset(label: String, appendsSuffix: Boolean): ChargeLine = ChargeLine(appendsSuffix = appendsSuffix, label = label, amount = MoneyFormat.zero, id = UUID.randomUUID())

        fun custom(): ChargeLine = ChargeLine(appendsSuffix = false, label = "", amount = MoneyFormat.zero, id = UUID.randomUUID())
    }

    val isPrintable: Boolean get() = amount != null && amount.signum() != 0 && label.isNotBlank()

    fun printedLabel(suffix: String): String = if (appendsSuffix && suffix.isNotBlank()) "$label $suffix" else label
}
