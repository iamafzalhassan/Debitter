package com.example.debitter.model

enum class DocumentKind(val directory: String, val filePrefix: String, val noun: String, val summary: String, val title: String) {
    DEBIT_NOTE(directory = "Debit Notes", filePrefix = "Debit-Note", noun = "debit note", summary = "Customs and Clearing Charges, Printed on A5", title = "Debit Note"),
    REFUND_LETTER(directory = "Refund Letters", filePrefix = "Refund-Letter", noun = "refund letter", summary = "Container Deposit Refund Request, Printed on A4", title = "Refund Letter"),
}
