package com.example.debitter.data

import com.example.debitter.model.ChargeSection

object ChargePresets {
    private val other: List<String> = listOf(
        "CLEARANCE",
        "DOCUMENTATION",
        "EXAMINATION",
        "ENTRY PASSING",
        "FREIGHT",
        "HANDLING",
        "MISSALATION",
        "SCREENING UNIT",
        "TRANSPORT",
        "TRANSPORT DETENTION",
        "UNLOADING",
        "VALUATION",
    )

    private val statutory: List<String> = listOf(
        "AGENCY FEE / SERVICE",
        "AGENT DO",
        "AIR LANKA",
        "AIR LINE DO",
        "ANIMAL QUARANTINE",
        "CONTAINER DEMURRAGE",
        "CONTAINER OT",
        "CONTAINER WEIGHT",
        "CUSTOMS DUTY",
        "CUSTOMS OT",
        "FREIGHT",
        "GRAYLINE",
        "IMPORT CONTROL",
        "SLPA",
        "SLSI",
    )

    private val terminal: Set<String> = setOf("AGENCY FEE / SERVICE", "CUSTOMS DUTY", "CUSTOMS OT", "TRANSPORT DETENTION")

    fun appendsSuffix(label: String): Boolean = label !in terminal

    fun labels(section: ChargeSection): List<String> = when (section) {
        ChargeSection.OTHER -> other
        ChargeSection.STATUTORY -> statutory
    }
}
