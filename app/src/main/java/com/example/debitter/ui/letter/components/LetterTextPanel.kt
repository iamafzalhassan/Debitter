package com.example.debitter.ui.letter.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import com.example.debitter.model.LetterLabelField
import com.example.debitter.model.LetterLabels
import com.example.debitter.model.value
import com.example.debitter.ui.components.AppTextField
import com.example.debitter.ui.components.ExpandablePanel

@Composable
fun LetterTextPanel(isExpanded: Boolean, onToggle: () -> Unit, onLabelChange: (LetterLabelField, String) -> Unit, labels: LetterLabels, modifier: Modifier = Modifier) {
    ExpandablePanel(modifier = modifier, isExpanded = isExpanded, onToggle = onToggle, subtitle = "Title, Letter Body, Every Printed Label", title = "Document Text") {
        for (field in LetterLabelField.entries) {
            AppTextField(
                capitalization = if (field.isUppercase) KeyboardCapitalization.Characters else KeyboardCapitalization.Sentences,
                label = field.caption,
                onValueChange = { onLabelChange(field, it) },
                singleLine = !field.isMultiline,
                value = labels.value(field),
            )
        }
    }
}
