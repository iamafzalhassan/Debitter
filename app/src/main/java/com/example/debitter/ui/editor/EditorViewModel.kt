package com.example.debitter.ui.editor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.debitter.data.ChargePresets
import com.example.debitter.data.Defaults
import com.example.debitter.model.ChargeLine
import com.example.debitter.model.ChargeSection
import com.example.debitter.model.DebitNote
import com.example.debitter.model.with
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.util.UUID

class EditorViewModel(private val savedState: SavedStateHandle) : ViewModel() {
    companion object {
        const val STATE_KEY: String = "editor-note"
    }

    private val mutableState: MutableStateFlow<DebitNote> = MutableStateFlow(savedState.get<DebitNote>(STATE_KEY) ?: Defaults.note())

    val state: StateFlow<DebitNote> = mutableState.asStateFlow()

    fun onEvent(event: EditorEvent) {
        when (event) {
            is EditorEvent.AddCharge -> updateLines(event.section) { it + ChargeLine.custom() }
            is EditorEvent.LoadNote -> update { withPresets(event.note.copy(header = event.note.header.copy(date = LocalDate.now()))) }
            is EditorEvent.Reset -> update { Defaults.note() }
            is EditorEvent.SetAdvance -> update { it.copy(advanceReceived = event.amount) }
            is EditorEvent.SetChargeAmount -> updateLines(event.section) { lines -> lines.map { if (it.id == event.id) it.copy(amount = event.amount) else it } }
            is EditorEvent.SetChargeLabel -> updateLines(event.section) { lines -> lines.map { if (it.id == event.id) it.copy(label = event.label) else it } }
            is EditorEvent.SetCompanyField -> update { it.copy(company = it.company.with(event.field, event.value)) }
            is EditorEvent.SetHeaderField -> update { it.copy(header = it.header.with(event.field, event.value)) }
            is EditorEvent.SetLabel -> update { it.copy(labels = it.labels.with(event.field, event.value)) }
        }
    }

    private fun withPresets(note: DebitNote): DebitNote = note.copy(other = rostered(note.other, ChargeSection.OTHER), statutory = rostered(note.statutory, ChargeSection.STATUTORY))

    private fun rostered(current: List<ChargeLine>, section: ChargeSection): List<ChargeLine> {
        val claimed = mutableSetOf<UUID>()
        val presets = ChargePresets.labels(section).map { label ->
            val match = current.firstOrNull { it.label == label && it.id !in claimed }

            if (match == null) ChargeLine.preset(label = label, appendsSuffix = ChargePresets.appendsSuffix(label)) else match.also { claimed += it.id }
        }

        return presets + current.filter { it.id !in claimed }
    }

    private fun updateLines(section: ChargeSection, block: (List<ChargeLine>) -> List<ChargeLine>) = update { it.withLines(section, block(it.lines(section))) }

    private fun update(block: (DebitNote) -> DebitNote) {
        val next = withValidAdvance(block(mutableState.value))

        mutableState.value = next
        savedState[STATE_KEY] = next
    }

    private fun withValidAdvance(note: DebitNote): DebitNote {
        val advance = note.advanceReceived ?: return note
        val subTotal = note.subTotal

        if (advance <= subTotal) return note
        return note.copy(advanceReceived = subTotal.takeIf { it.signum() > 0 })
    }
}
