package com.example.debitter.data

import com.example.debitter.model.ChargeSection
import com.example.debitter.model.ShipmentType

object ChargePresets {
    private val commonOther: List<String> = listOf(
        "DOCUMENTATION",
        "EXAMINATION",
        "ENTRY PASSING",
        "HANDLING",
        "MISSALATION",
        "SCREENING UNIT",
        "TRANSPORT",
        "TRANSPORT DETENTION",
        "VALUATION",
    )

    private val commonStatutory: List<String> = listOf(
        "AGENCY FEE / SERVICE",
        "AGENT DO",
        "ANIMAL QUARANTINE",
        "CONTAINER DEMURRAGE",
        "CONTAINER OT",
        "CONTAINER WEIGHT",
        "CUSTOMS DUTY",
        "CUSTOMS OT",
        "GRAYLINE",
        "IMPORT CONTROL",
        "SLPA",
        "SLSI",
    )

    private val otherAdditions: Map<ShipmentType, List<String>> = mapOf(
        ShipmentType.AIR_FREIGHT to emptyList(),
        ShipmentType.BLANK to emptyList(),
        ShipmentType.CONTAINER to emptyList(),
        ShipmentType.LCL to emptyList(),
        ShipmentType.PERSONAL to listOf("CLEARANCE", "FREIGHT", "UNLOADING"),
    )

    private val statutoryAdditions: Map<ShipmentType, List<String>> = mapOf(
        ShipmentType.AIR_FREIGHT to listOf("AIR LANKA", "AIR LINE DO", "FREIGHT"),
        ShipmentType.BLANK to emptyList(),
        ShipmentType.CONTAINER to listOf("FREIGHT"),
        ShipmentType.LCL to listOf("FREIGHT"),
        ShipmentType.PERSONAL to emptyList(),
    )

    private val terminal: Set<String> = setOf("AGENCY FEE / SERVICE", "CUSTOMS DUTY", "CUSTOMS OT", "TRANSPORT DETENTION")

    fun appendsSuffix(label: String): Boolean = label !in terminal

    fun labels(section: ChargeSection, type: ShipmentType): List<String> = when (section) {
        ChargeSection.OTHER -> commonOther + otherAdditions.getValue(type)
        ChargeSection.STATUTORY -> commonStatutory + statutoryAdditions.getValue(type)
    }
}
