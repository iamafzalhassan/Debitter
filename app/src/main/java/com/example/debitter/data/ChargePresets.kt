package com.example.debitter.data

import com.example.debitter.model.ChargeSection
import com.example.debitter.model.ShipmentType

object ChargePresets {
    private val commonOther: List<String> = listOf(
        "Documentation charges",
        "Examination expenses",
        "Entry Passing expenses",
        "Handling charges",
        "Missalation expenses",
        "Screening Unit expenses",
        "Transport charges",
        "Transport Detention",
        "Valuation expenses",
    )

    private val commonStatutory: List<String> = listOf(
        "Agency Fee / Service",
        "Agent DO charges",
        "Animal Quarantine charges",
        "Container Demurrage charges",
        "Container OT charges",
        "Container Weight charges",
        "Customs Duty",
        "Customs OT",
        "Grayline charges",
        "Import Control charges",
        "SLPA charges",
        "SLSI charges",
    )

    private val otherAdditions: Map<ShipmentType, List<String>> = mapOf(
        ShipmentType.AIR_FREIGHT to emptyList(),
        ShipmentType.BLANK to emptyList(),
        ShipmentType.CONTAINER to emptyList(),
        ShipmentType.LCL to emptyList(),
        ShipmentType.PERSONAL to listOf("Clearance expenses", "Freight charges", "Unloading expenses"),
    )

    private val statutoryAdditions: Map<ShipmentType, List<String>> = mapOf(
        ShipmentType.AIR_FREIGHT to listOf("Air Lanka charges", "Air Line DO charges", "Freight charges"),
        ShipmentType.BLANK to emptyList(),
        ShipmentType.CONTAINER to listOf("Freight charges"),
        ShipmentType.LCL to listOf("Freight charges"),
        ShipmentType.PERSONAL to emptyList(),
    )

    fun labels(section: ChargeSection, type: ShipmentType): List<String> = when (section) {
        ChargeSection.OTHER -> commonOther + otherAdditions.getValue(type)
        ChargeSection.STATUTORY -> commonStatutory + statutoryAdditions.getValue(type)
    }
}
