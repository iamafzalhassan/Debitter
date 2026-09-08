package com.example.debitter.ui.editor.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.debitter.model.HeaderField
import com.example.debitter.model.NoteHeader
import com.example.debitter.model.NoteLabels
import com.example.debitter.model.value
import com.example.debitter.ui.components.AppStaticField
import com.example.debitter.ui.components.AppTextField
import com.example.debitter.ui.theme.AppSpacing
import com.example.debitter.util.DateFormat

@Composable
fun HeaderFields(onFieldChange: (HeaderField, String) -> Unit, header: NoteHeader, labels: NoteLabels, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(AppSpacing.md)) {
        AppStaticField(label = labels.date, value = DateFormat.format(header.date))
        for (field in HeaderField.entries) {
            AppTextField(
                label = labels.value(field),
                value = header.value(field),
                onValueChange = { onFieldChange(field, it) },
            )
        }
    }
}
