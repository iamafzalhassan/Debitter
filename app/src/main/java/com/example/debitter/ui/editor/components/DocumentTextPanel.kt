package com.example.debitter.ui.editor.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import com.example.debitter.model.CompanyBlock
import com.example.debitter.model.CompanyField
import com.example.debitter.model.LabelField
import com.example.debitter.model.NoteLabels
import com.example.debitter.model.value
import com.example.debitter.ui.components.AppTextField
import com.example.debitter.ui.components.DottedDivider
import com.example.debitter.ui.theme.AppColors
import com.example.debitter.ui.theme.AppSpacing
import com.example.debitter.ui.theme.AppTextStyles

private const val HALF_TURN: Float = 180f

@Composable
fun DocumentTextPanel(
    isExpanded: Boolean,
    onCompanyChange: (CompanyField, String) -> Unit,
    onLabelChange: (LabelField, String) -> Unit,
    onToggle: () -> Unit,
    company: CompanyBlock,
    labels: NoteLabels,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(AppSpacing.radiusCard)
    val rotation by animateFloatAsState(targetValue = if (isExpanded) HALF_TURN else 0f, label = "chevron")

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(AppColors.surfaceCard)
            .border(border = BorderStroke(AppSpacing.hairline, AppColors.divider), shape = shape),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().clickable(onClick = onToggle).padding(horizontal = AppSpacing.lg, vertical = AppSpacing.lg),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(maxLines = 1, overflow = TextOverflow.Ellipsis, style = AppTextStyles.listPrimary, text = "Document text")
                Spacer(modifier = Modifier.height(AppSpacing.xxs))
                Text(maxLines = 1, overflow = TextOverflow.Ellipsis, style = AppTextStyles.listSecondary, text = "Company block, title, every printed label")
            }
            Icon(
                modifier = Modifier.size(AppSpacing.iconPlaceholder).rotate(rotation),
                contentDescription = null,
                imageVector = Icons.Filled.KeyboardArrowDown,
                tint = AppColors.textSecondary,
            )
        }
        AnimatedVisibility(visible = isExpanded) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(start = AppSpacing.lg, end = AppSpacing.lg, bottom = AppSpacing.lg),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
            ) {
                for (field in CompanyField.entries) {
                    AppTextField(
                        label = field.caption,
                        value = company.value(field),
                        onValueChange = { onCompanyChange(field, it) },
                        capitalization = KeyboardCapitalization.Characters,
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
                        capitalization = KeyboardCapitalization.Words,
                    )
                }
            }
        }
    }
}