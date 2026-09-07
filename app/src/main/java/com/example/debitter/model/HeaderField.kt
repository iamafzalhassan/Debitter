package com.example.debitter.model

enum class HeaderField { BILL_TO, VESSEL_FLIGHT, CUSTOMS_ENTRY, CONTAINER_NO, BL_AWB_NO, VOYAGE_NO_DATE, CONSIGNMENT }

fun NoteHeader.value(field: HeaderField): String = when (field) {
    HeaderField.BILL_TO -> billTo
    HeaderField.BL_AWB_NO -> blAwbNo
    HeaderField.CONSIGNMENT -> consignment
    HeaderField.CONTAINER_NO -> containerNo
    HeaderField.CUSTOMS_ENTRY -> customsEntry
    HeaderField.VESSEL_FLIGHT -> vesselFlight
    HeaderField.VOYAGE_NO_DATE -> voyageNoDate
}

fun NoteHeader.with(field: HeaderField, value: String): NoteHeader = when (field) {
    HeaderField.BILL_TO -> copy(billTo = value)
    HeaderField.BL_AWB_NO -> copy(blAwbNo = value)
    HeaderField.CONSIGNMENT -> copy(consignment = value)
    HeaderField.CONTAINER_NO -> copy(containerNo = value)
    HeaderField.CUSTOMS_ENTRY -> copy(customsEntry = value)
    HeaderField.VESSEL_FLIGHT -> copy(vesselFlight = value)
    HeaderField.VOYAGE_NO_DATE -> copy(voyageNoDate = value)
}

fun NoteLabels.value(field: HeaderField): String = when (field) {
    HeaderField.BILL_TO -> billTo
    HeaderField.BL_AWB_NO -> blAwbNo
    HeaderField.CONSIGNMENT -> consignment
    HeaderField.CONTAINER_NO -> containerNo
    HeaderField.CUSTOMS_ENTRY -> customsEntry
    HeaderField.VESSEL_FLIGHT -> vesselFlight
    HeaderField.VOYAGE_NO_DATE -> voyageNoDate
}
