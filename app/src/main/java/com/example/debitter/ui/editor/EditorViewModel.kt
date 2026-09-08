package com.example.debitter.ui.editor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.debitter.data.ChargePresets
import com.example.debitter.data.Defaults
import com.example.debitter.model.ChargeLine
import com.example.debitter.model.ChargeSection
import com.example.debitter.model.DebitNote
import com.example.debitter.model.ShipmentType
import com.example.debitter.model.with
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.Serializable

data class EditorState(val note: DebitNote, val shipmentType: ShipmentType) : Serializable

class EditorViewModel(private val savedState: SavedStateHandle) : ViewModel() {
    companion object {
        const val STATE_KEY: String = "editor-state"
    }

    private val mutableState: MutableStateFlow<EditorState> = MutableStateFlow(savedState.get<EditorState>(STATE_KEY) ?: initial())

    val state: StateFlow<EditorState> = mutableState.asStateFlow()

    fun onEvent(event: EditorEvent) {
        when (event) {
            is EditorEvent.AddCharge -> updateLines(event.section) { it + ChargeLine.custom() }
            is EditorEvent.Reset -> update { initial() }
            is EditorEvent.SetAdvance -> updateNote { it.copy(advanceReceived = event.amount) }
            is EditorEvent.SetChargeAmount -> updateLines(event.section) { lines -> lines.map { if (it.id == event.id) it.copy(amount = event.amount) else it } }
            is EditorEvent.SetChargeLabel -> updateLines(event.section) { lines -> lines.map { if (it.id == event.id) it.copy(label = event.label) else it } }
            is EditorEvent.SetCompanyField -> updateNote { it.copy(company = it.company.with(event.field, event.value)) }
            is EditorEvent.SetHeaderField -> updateNote { it.copy(header = it.header.with(event.field, event.value)) }
            is EditorEvent.SetLabel -> updateNote { it.copy(labels = it.labels.with(event.field, event.value)) }
            is EditorEvent.SetShipmentType -> update { applyShipmentType(it, event.type) }
        }
    }

    private fun updateLines(section: ChargeSection, block: (List<ChargeLine>) -> List<ChargeLine>) = updateNote { it.withLines(section, block(it.lines(section))) }

    private fun updateNote(block: (DebitNote) -> DebitNote) = update { it.copy(note = block(it.note)) }

    private fun update(block: (EditorState) -> EditorState) {
        val next = block(mutableState.value)

        mutableState.value = next
        savedState[STATE_KEY] = next
    }

    private fun applyShipmentType(state: EditorState, type: ShipmentType): EditorState {
        if (state.shipmentType == type) return state

        val note = state.note
        val other = reroster(note.other, ChargeSection.OTHER, state.shipmentType, type)
        val statutory = reroster(note.statutory, ChargeSection.STATUTORY, state.shipmentType, type)

        return state.copy(note = note.copy(other = other, statutory = statutory), shipmentType = type)
    }

    private fun reroster(current: List<ChargeLine>, section: ChargeSection, from: ShipmentType, to: ShipmentType): List<ChargeLine> {
        val outgoing = ChargePresets.labels(section, from).toSet()
        val incoming = ChargePresets.labels(section, to)
        val incomingSet = incoming.toSet()
        val byLabel = current.associateBy { it.label }
        val rostered = incoming.map { byLabel[it] ?: ChargeLine.preset(label = it, appendsSuffix = ChargePresets.appendsSuffix(it)) }
        val carried = current.filter { it.label !in incomingSet && (it.label !in outgoing || it.isPrintable) }

        return rostered + carried
    }

    private fun initial(): EditorState = EditorState(note = Defaults.note(), shipmentType = Defaults.shipmentType)
}
