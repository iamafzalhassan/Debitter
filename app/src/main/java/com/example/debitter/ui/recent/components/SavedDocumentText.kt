package com.example.debitter.ui.recent.components

import com.example.debitter.model.SavedDocument
import com.example.debitter.model.SavedLetter
import com.example.debitter.model.SavedNote
import com.example.debitter.util.MoneyFormat

val SavedDocument.heading: String get() = when (this) {
    is SavedLetter -> agent.ifBlank { letter.labels.title }
    is SavedNote -> billTo.ifBlank { note.labels.title }
}

val SavedDocument.summary: String get() = when (this) {
    is SavedLetter -> customer
    is SavedNote -> MoneyFormat.format(total)
}
