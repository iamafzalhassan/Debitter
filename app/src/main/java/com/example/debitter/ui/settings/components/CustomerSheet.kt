package com.example.debitter.ui.settings.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardCapitalization
import com.example.debitter.model.CustomerEntry
import com.example.debitter.model.Letterhead
import com.example.debitter.model.LetterheadField
import com.example.debitter.model.value
import com.example.debitter.model.with
import com.example.debitter.ui.components.AppTextField

@Composable
fun CustomerSheet(onDelete: () -> Unit, onDismiss: () -> Unit, onSave: (Letterhead) -> Unit, entry: CustomerEntry?) {
    var draft by remember { mutableStateOf(entry?.letterhead ?: Letterhead(addressLine = "", contactLine = "", name = "", tagline = "")) }

    EntrySheet(isNew = entry == null, isValid = draft.name.isNotBlank(), noun = "Customer", onDelete = onDelete, onDismiss = onDismiss, onSave = { onSave(draft) }) {
        for (field in LetterheadField.entries) {
            AppTextField(
                capitalization = if (field.isUppercase) KeyboardCapitalization.Characters else KeyboardCapitalization.Words,
                label = field.caption,
                onValueChange = { draft = draft.with(field, it) },
                value = draft.value(field),
            )
        }
    }
}
