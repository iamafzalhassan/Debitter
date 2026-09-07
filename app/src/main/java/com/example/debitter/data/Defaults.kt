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
        advanceReceived = "Advanced Received",
        billTo = "To",
        blAwbNo = "BL/AWB No",
        consignment = "Consignment",
        containerNo = "Container No",
        customsEntry = "Customs Entry",
        date = "Date",
        otherSection = "Other",
        signature = "Proprietor's Signature.",
        statutorySection = "Statutory",
        subTotal = "Sub Total",
        title = "D E B I T   N O T E",
        total = "Total",
        vesselFlight = "Vessel/Flight",
        voyageNoDate = "Voyage No/Date",
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

    fun lines(section: ChargeSection, type: ShipmentType): List<ChargeLine> = ChargePresets.labels(section, type).map { ChargeLine.empty(it) }

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
