package com.example.debitter.model

enum class ReferenceField { CONTAINER_NO, BL_NO, VESSEL, VOYAGE, RECEIPT_NO }

fun LetterReferences.value(field: ReferenceField): String = when (field) {
    ReferenceField.BL_NO -> blNo
    ReferenceField.CONTAINER_NO -> containerNo
    ReferenceField.RECEIPT_NO -> receiptNo
    ReferenceField.VESSEL -> vessel
    ReferenceField.VOYAGE -> voyage
}

fun LetterReferences.with(field: ReferenceField, value: String): LetterReferences = when (field) {
    ReferenceField.BL_NO -> copy(blNo = value)
    ReferenceField.CONTAINER_NO -> copy(containerNo = value)
    ReferenceField.RECEIPT_NO -> copy(receiptNo = value)
    ReferenceField.VESSEL -> copy(vessel = value)
    ReferenceField.VOYAGE -> copy(voyage = value)
}

fun LetterLabels.value(field: ReferenceField): String = when (field) {
    ReferenceField.BL_NO -> blNo
    ReferenceField.CONTAINER_NO -> containerNo
    ReferenceField.RECEIPT_NO -> receiptNo
    ReferenceField.VESSEL -> vessel
    ReferenceField.VOYAGE -> voyage
}
