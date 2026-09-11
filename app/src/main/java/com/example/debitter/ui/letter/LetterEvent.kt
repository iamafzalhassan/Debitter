package com.example.debitter.ui.letter

import com.example.debitter.model.AgentField
import com.example.debitter.model.LetterLabelField
import com.example.debitter.model.Letterhead
import com.example.debitter.model.LetterheadField
import com.example.debitter.model.ReferenceField
import com.example.debitter.model.RefundLetter
import com.example.debitter.model.ShippingAgent

sealed interface LetterEvent {
    data class LoadLetter(val letter: RefundLetter) : LetterEvent

    data object Reset : LetterEvent

    data class SelectAgent(val agent: ShippingAgent) : LetterEvent

    data class SelectLetterhead(val letterhead: Letterhead) : LetterEvent

    data class SetAgentField(val value: String, val field: AgentField) : LetterEvent

    data class SetLabel(val value: String, val field: LetterLabelField) : LetterEvent

    data class SetLetterheadField(val value: String, val field: LetterheadField) : LetterEvent

    data class SetReferenceField(val value: String, val field: ReferenceField) : LetterEvent
}
