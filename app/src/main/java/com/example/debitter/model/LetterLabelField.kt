package com.example.debitter.model

enum class LetterLabelField(val caption: String, val key: String) {
    ATTENTION("Attention Line", "attention"),
    TITLE("Document Title", "title"),
    CONTAINER_NO("Container No Label", "containerNo"),
    BL_NO("BL No Label", "blNo"),
    VESSEL("Vessel Label", "vessel"),
    VOYAGE("Voyage Label", "voyage"),
    RECEIPT_NO("Receipt No Label", "receiptNo"),
    SALUTATION("Salutation Line", "salutation"),
    BODY("Letter Body", "body"),
    EMPHASIS("Bold Words", "emphasis"),
    CLOSING("Closing Line", "closing"),
    SIGN_OFF("Sign-Off Line", "signOff"),
    SIGNATORY_TITLE("Signatory Title", "signatoryTitle"),
    SIGNATORY_NAME("Signatory Name", "signatoryName"),
    SIGNATORY_PHONE("Signatory Phone", "signatoryPhone");

    val isMultiline: Boolean get() = this == BODY
    val isUppercase: Boolean get() = this == TITLE
}

fun LetterLabels.value(field: LetterLabelField): String = when (field) {
    LetterLabelField.ATTENTION -> attention
    LetterLabelField.BL_NO -> blNo
    LetterLabelField.BODY -> body
    LetterLabelField.CLOSING -> closing
    LetterLabelField.CONTAINER_NO -> containerNo
    LetterLabelField.EMPHASIS -> emphasis
    LetterLabelField.RECEIPT_NO -> receiptNo
    LetterLabelField.SALUTATION -> salutation
    LetterLabelField.SIGNATORY_NAME -> signatoryName
    LetterLabelField.SIGNATORY_PHONE -> signatoryPhone
    LetterLabelField.SIGNATORY_TITLE -> signatoryTitle
    LetterLabelField.SIGN_OFF -> signOff
    LetterLabelField.TITLE -> title
    LetterLabelField.VESSEL -> vessel
    LetterLabelField.VOYAGE -> voyage
}

fun LetterLabels.with(field: LetterLabelField, value: String): LetterLabels = when (field) {
    LetterLabelField.ATTENTION -> copy(attention = value)
    LetterLabelField.BL_NO -> copy(blNo = value)
    LetterLabelField.BODY -> copy(body = value)
    LetterLabelField.CLOSING -> copy(closing = value)
    LetterLabelField.CONTAINER_NO -> copy(containerNo = value)
    LetterLabelField.EMPHASIS -> copy(emphasis = value)
    LetterLabelField.RECEIPT_NO -> copy(receiptNo = value)
    LetterLabelField.SALUTATION -> copy(salutation = value)
    LetterLabelField.SIGNATORY_NAME -> copy(signatoryName = value)
    LetterLabelField.SIGNATORY_PHONE -> copy(signatoryPhone = value)
    LetterLabelField.SIGNATORY_TITLE -> copy(signatoryTitle = value)
    LetterLabelField.SIGN_OFF -> copy(signOff = value)
    LetterLabelField.TITLE -> copy(title = value)
    LetterLabelField.VESSEL -> copy(vessel = value)
    LetterLabelField.VOYAGE -> copy(voyage = value)
}
