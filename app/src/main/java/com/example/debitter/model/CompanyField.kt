package com.example.debitter.model

enum class CompanyField(val caption: String) {
    NAME("Company name"),
    ADDRESS_LINE("Address line"),
    CONTACT_LINE("Contact line"),
}

fun CompanyBlock.value(field: CompanyField): String = when (field) {
    CompanyField.ADDRESS_LINE -> addressLine
    CompanyField.CONTACT_LINE -> contactLine
    CompanyField.NAME -> name
}

fun CompanyBlock.with(field: CompanyField, value: String): CompanyBlock = when (field) {
    CompanyField.ADDRESS_LINE -> copy(addressLine = value)
    CompanyField.CONTACT_LINE -> copy(contactLine = value)
    CompanyField.NAME -> copy(name = value)
}
