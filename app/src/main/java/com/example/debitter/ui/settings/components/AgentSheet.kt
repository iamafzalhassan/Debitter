package com.example.debitter.ui.settings.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardCapitalization
import com.example.debitter.model.AgentEntry
import com.example.debitter.model.AgentField
import com.example.debitter.model.ShippingAgent
import com.example.debitter.model.value
import com.example.debitter.model.with
import com.example.debitter.ui.components.AppTextField

@Composable
fun AgentSheet(onDelete: () -> Unit, onDismiss: () -> Unit, onSave: (ShippingAgent) -> Unit, entry: AgentEntry?) {
    var draft by remember { mutableStateOf(entry?.agent ?: ShippingAgent(address = "", name = "")) }

    EntrySheet(isNew = entry == null, isValid = draft.name.isNotBlank(), noun = "Shipping Agent", onDelete = onDelete, onDismiss = onDismiss, onSave = { onSave(draft) }) {
        for (field in AgentField.entries) {
            AppTextField(
                capitalization = KeyboardCapitalization.Words,
                label = field.caption,
                onValueChange = { draft = draft.with(field, it) },
                singleLine = !field.isMultiline,
                value = draft.value(field),
            )
        }
    }
}
