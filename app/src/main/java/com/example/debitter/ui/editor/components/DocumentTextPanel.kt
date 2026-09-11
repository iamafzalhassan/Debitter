package com.example.debitter.ui.editor.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.debitter.model.CompanyBlock
import com.example.debitter.model.CompanyField
import com.example.debitter.model.LabelField
import com.example.debitter.model.NoteLabels
import com.example.debitter.model.value
import com.example.debitter.ui.components.AppTextField
import com.example.debitter.ui.components.DottedDivider
import com.example.debitter.ui.components.ExpandablePanel
import com.example.debitter.ui.theme.AppSpacing

@Composable
fun DocumentTextPanel(
    isExpanded: Boolean,
    onToggle: () -> Unit,
    onCompanyChange: (CompanyField, String) -> Unit,
    onLabelChange: (LabelField, String) -> Unit,
    company: CompanyBlock,
    labels: NoteLabels,
    modifier: Modifier = Modifier,
) {
    ExpandablePanel(modifier = modifier, isExpanded = isExpanded, onToggle = onToggle, subtitle = "Company Block, Title, Every Printed Label", title = "Document Text") {
        for (field in CompanyField.entries) {
            AppTextField(
                label = field.caption,
                value = company.value(field),
                onValueChange = { onCompanyChange(field, it) },
            )
        }
        Spacer(modifier = Modifier.height(AppSpacing.xs))
        DottedDivider()
        Spacer(modifier = Modifier.height(AppSpacing.xs))
        for (field in LabelField.entries) {
            AppTextField(
                label = field.caption,
                value = labels.value(field),
                onValueChange = { onLabelChange(field, it) },
            )
        }
    }
}
