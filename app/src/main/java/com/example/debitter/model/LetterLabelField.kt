package com.example.debitter.model

enum class LetterLabelField(val caption: String) {
    ATTENTION("Attention Line"),
    TITLE("Document Title"),
    CONTAINER_NO("Container No Label"),
    BL_NO("BL No Label"),
    VESSEL("Vessel Label"),
    VOYAGE("Voyage Label"),
    RECEIPT_NO("Receipt No Label"),
    SALUTATION("Salutation Line"),
    BODY("Letter Body"),
    EMPHASIS("Bold Words"),
    CLOSING("Closing Line"),
    SIGN_OFF("Sign-Off Line"),
    SIGNATORY_TITLE("Signatory Title"),
    SIGNATORY_NAME("Signatory Name"),
    SIGNATORY_PHONE("Signatory Phone"),
    ;

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
