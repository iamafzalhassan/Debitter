package com.example.debitter.model

enum class LetterheadField(val caption: String) {
    NAME("Company Name"),
    TAGLINE("Tagline"),
    ADDRESS_LINE("Address Line"),
    CONTACT_LINE("Contact Line"),
    ;

    val isUppercase: Boolean get() = this != TAGLINE
}

fun Letterhead.value(field: LetterheadField): String = when (field) {
    LetterheadField.ADDRESS_LINE -> addressLine
    LetterheadField.CONTACT_LINE -> contactLine
    LetterheadField.NAME -> name
    LetterheadField.TAGLINE -> tagline
}

fun Letterhead.with(field: LetterheadField, value: String): Letterhead = when (field) {
    LetterheadField.ADDRESS_LINE -> copy(addressLine = value)
    LetterheadField.CONTACT_LINE -> copy(contactLine = value)
    LetterheadField.NAME -> copy(name = value)
    LetterheadField.TAGLINE -> copy(tagline = value)
}
