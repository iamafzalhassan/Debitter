package com.example.debitter.model

enum class LabelField(val caption: String) {
    TITLE("Document title"),
    DATE("Date label"),
    BILL_TO("To label"),
    VESSEL_FLIGHT("Vessel/Flight label"),
    CUSTOMS_ENTRY("Customs Entry label"),
    CONTAINER_NO("Container No label"),
    BL_AWB_NO("BL/AWB No label"),
    VOYAGE_NO_DATE("Voyage No/Date label"),
    CONSIGNMENT("Consignment label"),
    STATUTORY_SECTION("Statutory section heading"),
    OTHER_SECTION("Other section heading"),
    SUB_TOTAL("Sub Total label"),
    ADVANCE_RECEIVED("Advanced Received label"),
    TOTAL("Total label"),
    SIGNATURE("Signature caption"),
}

fun NoteLabels.value(field: LabelField): String = when (field) {
    LabelField.ADVANCE_RECEIVED -> advanceReceived
    LabelField.BILL_TO -> billTo
    LabelField.BL_AWB_NO -> blAwbNo
    LabelField.CONSIGNMENT -> consignment
    LabelField.CONTAINER_NO -> containerNo
    LabelField.CUSTOMS_ENTRY -> customsEntry
    LabelField.DATE -> date
    LabelField.OTHER_SECTION -> otherSection
    LabelField.SIGNATURE -> signature
    LabelField.STATUTORY_SECTION -> statutorySection
    LabelField.SUB_TOTAL -> subTotal
    LabelField.TITLE -> title
    LabelField.TOTAL -> total
    LabelField.VESSEL_FLIGHT -> vesselFlight
    LabelField.VOYAGE_NO_DATE -> voyageNoDate
}

fun NoteLabels.with(field: LabelField, value: String): NoteLabels = when (field) {
    LabelField.ADVANCE_RECEIVED -> copy(advanceReceived = value)
    LabelField.BILL_TO -> copy(billTo = value)
    LabelField.BL_AWB_NO -> copy(blAwbNo = value)
    LabelField.CONSIGNMENT -> copy(consignment = value)
    LabelField.CONTAINER_NO -> copy(containerNo = value)
    LabelField.CUSTOMS_ENTRY -> copy(customsEntry = value)
    LabelField.DATE -> copy(date = value)
    LabelField.OTHER_SECTION -> copy(otherSection = value)
    LabelField.SIGNATURE -> copy(signature = value)
    LabelField.STATUTORY_SECTION -> copy(statutorySection = value)
    LabelField.SUB_TOTAL -> copy(subTotal = value)
    LabelField.TITLE -> copy(title = value)
    LabelField.TOTAL -> copy(total = value)
    LabelField.VESSEL_FLIGHT -> copy(vesselFlight = value)
    LabelField.VOYAGE_NO_DATE -> copy(voyageNoDate = value)
}
