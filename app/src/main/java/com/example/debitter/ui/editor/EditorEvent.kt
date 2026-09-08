package com.example.debitter.ui.editor

import com.example.debitter.model.ChargeSection
import com.example.debitter.model.CompanyField
import com.example.debitter.model.DebitNote
import com.example.debitter.model.HeaderField
import com.example.debitter.model.LabelField
import com.example.debitter.model.ShipmentType
import java.math.BigDecimal
import java.util.UUID

sealed interface EditorEvent {
    data class AddCharge(val section: ChargeSection) : EditorEvent

    data class LoadNote(val note: DebitNote) : EditorEvent

    data object Reset : EditorEvent

    data class SetAdvance(val amount: BigDecimal?) : EditorEvent

    data class SetChargeAmount(val amount: BigDecimal?, val section: ChargeSection, val id: UUID) : EditorEvent

    data class SetChargeLabel(val label: String, val section: ChargeSection, val id: UUID) : EditorEvent

    data class SetCompanyField(val value: String, val field: CompanyField) : EditorEvent

    data class SetHeaderField(val value: String, val field: HeaderField) : EditorEvent

    data class SetLabel(val value: String, val field: LabelField) : EditorEvent

    data class SetShipmentType(val type: ShipmentType) : EditorEvent
}
