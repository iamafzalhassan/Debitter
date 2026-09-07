package com.example.debitter.ui.editor.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import com.example.debitter.model.HeaderField
import com.example.debitter.model.NoteHeader
import com.example.debitter.model.NoteLabels
import com.example.debitter.model.value
import com.example.debitter.ui.components.AppDisplayField
import com.example.debitter.ui.components.AppTextField
import com.example.debitter.ui.theme.AppSpacing
import com.example.debitter.util.DateFormat
import java.time.LocalDate

@Composable
fun HeaderFields(
    onDateChange: (LocalDate?) -> Unit,
    onFieldChange: (HeaderField, String) -> Unit,
    header: NoteHeader,
    labels: NoteLabels,
    modifier: Modifier = Modifier,
) {
    var isPickerOpen by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
        AppDisplayField(label = labels.date, value = DateFormat.format(header.date), onClick = { isPickerOpen = true })
        for (field in HeaderField.entries) {
            AppTextField(
                label = labels.value(field),
                value = header.value(field),
                onValueChange = { onFieldChange(field, it) },
                capitalization = KeyboardCapitalization.Characters,
            )
        }
    }

    if (isPickerOpen) {
        DateSheet(date = header.date, onDismiss = { isPickerOpen = false }, onPick = onDateChange)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateSheet(onDismiss: () -> Unit, onPick: (LocalDate?) -> Unit, date: LocalDate?) {
    val state = rememberDatePickerState(initialSelectedDateMillis = DateFormat.toEpochMillis(date))

    DatePickerDialog(
        confirmButton = {
            TextButton(
                onClick = {
                    onPick(DateFormat.fromEpochMillis(state.selectedDateMillis))
                    onDismiss()
                },
            ) {
                Text(text = "Set")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(text = "Cancel") } },
        onDismissRequest = onDismiss,
    ) {
        DatePicker(state = state, showModeToggle = false)
    }
}
