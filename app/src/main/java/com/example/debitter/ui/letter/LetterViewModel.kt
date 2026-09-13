package com.example.debitter.ui.letter

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.debitter.data.Defaults
import com.example.debitter.model.RefundLetter
import com.example.debitter.model.with
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate

class LetterViewModel(private val savedState: SavedStateHandle) : ViewModel() {
    companion object {
        const val STATE_KEY: String = "editor-letter"
    }

    private val mutableState: MutableStateFlow<RefundLetter> = MutableStateFlow(savedState.get<RefundLetter>(STATE_KEY) ?: Defaults.letter())

    val state: StateFlow<RefundLetter> = mutableState.asStateFlow()

    fun onEvent(event: LetterEvent) {
        when (event) {
            is LetterEvent.LoadLetter -> update { event.letter.copy(date = LocalDate.now()) }
            is LetterEvent.Reset -> update { Defaults.letter() }
            is LetterEvent.SelectAgent -> update { it.copy(agent = event.agent) }
            is LetterEvent.SelectLetterhead -> update { it.copy(letterhead = event.letterhead) }
            is LetterEvent.SetAgentField -> update { it.copy(agent = it.agent.with(event.field, event.value)) }
            is LetterEvent.SetLabel -> update { it.copy(labels = it.labels.with(event.field, event.value)) }
            is LetterEvent.SetLetterheadField -> update { it.copy(letterhead = it.letterhead.with(event.field, event.value)) }
            is LetterEvent.SetReferenceField -> update { it.copy(references = it.references.with(event.field, event.value)) }
        }
    }

    private fun update(block: (RefundLetter) -> RefundLetter) {
        val next = block(mutableState.value)

        mutableState.value = next
        savedState[STATE_KEY] = next
    }
}
