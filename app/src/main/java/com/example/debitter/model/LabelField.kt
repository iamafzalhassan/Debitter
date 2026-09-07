package com.example.debitter.model

enum class LabelField(val caption: String) {
    TITLE("Document Title"),
    DATE("Date Label"),
    BILL_TO("To Label"),
    VESSEL_FLIGHT("Vessel/Flight Label"),
    CUSTOMS_ENTRY("Customs Entry Label"),
    CONTAINER_NO("Container No Label"),
    BL_AWB_NO("BL/AWB No Label"),
    VOYAGE_NO_DATE("Voyage No/Date Label"),
    CONSIGNMENT("Consignment Label"),
    STATUTORY_SECTION("Statutory Section Heading"),
    OTHER_SECTION("Other Section Heading"),
    CHARGE_SUFFIX("Charge Word"),
    SUB_TOTAL("Sub Total Label"),
    ADVANCE_RECEIVED("Advanced Received Label"),
    TOTAL("Total Label"),
    SIGNATURE("Signature Caption"),
}

fun NoteLabels.value(field: LabelField): String = when (field) {
    LabelField.ADVANCE_RECEIVED -> advanceReceived
    LabelField.BILL_TO -> billTo
    LabelField.BL_AWB_NO -> blAwbNo
    LabelField.CHARGE_SUFFIX -> chargeSuffix
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
    LabelField.CHARGE_SUFFIX -> copy(chargeSuffix = value)
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
