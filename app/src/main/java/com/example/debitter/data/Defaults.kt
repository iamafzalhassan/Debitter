package com.example.debitter.data

import com.example.debitter.model.ChargeLine
import com.example.debitter.model.ChargeSection
import com.example.debitter.model.CompanyBlock
import com.example.debitter.model.DebitNote
import com.example.debitter.model.NoteHeader
import com.example.debitter.model.NoteLabels
import com.example.debitter.model.ShipmentType
import java.time.LocalDate

object Defaults {
    val company: CompanyBlock = CompanyBlock(
        addressLine = "59/2, DEMATAGODA ROAD, MARADANA, COLOMBO, SRI LANKA.",
        contactLine = "TEL: +94 (77) 754 0094 | EMAIL: WORLDOFCARGO@OUTLOOK.COM",
        name = "CARGO WORLD",
    )

    val labels: NoteLabels = NoteLabels(
        advanceReceived = "ADVANCED RECEIVED",
        billTo = "TO",
        blAwbNo = "BL/AWB NO",
        chargeSuffix = "CHARGES",
        consignment = "CONSIGNMENT",
        containerNo = "CONTAINER NO",
        customsEntry = "CUSTOMS ENTRY",
        date = "DATE",
        otherSection = "OTHER",
        signature = "PROPRIETOR'S SIGNATURE.",
        statutorySection = "STATUTORY",
        subTotal = "SUB TOTAL",
        title = "DEBIT NOTE",
        total = "TOTAL",
        vesselFlight = "VESSEL/FLIGHT",
        voyageNoDate = "VOYAGE NO/DATE",
    )

    val shipmentType: ShipmentType = ShipmentType.CONTAINER

    fun note(type: ShipmentType = shipmentType, today: LocalDate = LocalDate.now()): DebitNote = DebitNote(
        other = lines(ChargeSection.OTHER, type),
        statutory = lines(ChargeSection.STATUTORY, type),
        advanceReceived = null,
        company = company,
        header = header(today),
        labels = labels,
    )

    fun lines(section: ChargeSection, type: ShipmentType): List<ChargeLine> = ChargePresets.labels(section, type).map { ChargeLine.preset(label = it, appendsSuffix = ChargePresets.appendsSuffix(it)) }

    private fun header(today: LocalDate): NoteHeader = NoteHeader(
        billTo = "",
        blAwbNo = "",
        consignment = "",
        containerNo = "",
        customsEntry = "",
        vesselFlight = "",
        voyageNoDate = "",
        date = today,
    )
}
