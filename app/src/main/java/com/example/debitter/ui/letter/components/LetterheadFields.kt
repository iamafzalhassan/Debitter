package com.example.debitter.ui.letter.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import com.example.debitter.model.Letterhead
import com.example.debitter.model.LetterheadField
import com.example.debitter.model.value
import com.example.debitter.ui.components.AppTextField
import com.example.debitter.ui.components.SecondaryButton
import com.example.debitter.ui.theme.AppSpacing

@Composable
fun LetterheadFields(onPick: () -> Unit, onFieldChange: (LetterheadField, String) -> Unit, letterhead: Letterhead, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(AppSpacing.md)) {
        SecondaryButton(modifier = Modifier.fillMaxWidth(), label = if (letterhead.name.isBlank()) "Choose Customer" else "Change Customer", onClick = onPick)
        for (field in LetterheadField.entries) {
            AppTextField(
                capitalization = if (field.isUppercase) KeyboardCapitalization.Characters else KeyboardCapitalization.Words,
                label = field.caption,
                onValueChange = { onFieldChange(field, it) },
                value = letterhead.value(field),
            )
        }
    }
}
