package com.example.debitter.ui.letter.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.debitter.model.LetterLabels
import com.example.debitter.model.LetterReferences
import com.example.debitter.model.ReferenceField
import com.example.debitter.model.value
import com.example.debitter.ui.components.AppStaticField
import com.example.debitter.ui.components.AppTextField
import com.example.debitter.ui.theme.AppSpacing
import com.example.debitter.util.DateFormat
import java.time.LocalDate

@Composable
fun ReferenceFields(onFieldChange: (ReferenceField, String) -> Unit, labels: LetterLabels, references: LetterReferences, date: LocalDate?, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(AppSpacing.md)) {
        AppStaticField(label = "Date", value = DateFormat.format(date))
        for (field in ReferenceField.entries) {
            AppTextField(
                label = labels.value(field),
                onValueChange = { onFieldChange(field, it) },
                singleLine = false,
                value = references.value(field),
            )
        }
    }
}
