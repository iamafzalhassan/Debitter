package com.example.debitter.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.ui.text.style.TextOverflow
import com.example.debitter.ui.theme.AppColors
import com.example.debitter.ui.theme.AppSpacing
import com.example.debitter.ui.theme.AppTextStyles

private const val HALF_TURN: Float = 180f

@Composable
fun ExpandablePanel(isExpanded: Boolean, subtitle: String, title: String, onToggle: () -> Unit, modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
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
            modifier = Modifier.fillMaxWidth().clickable(onClick = onToggle).padding(horizontal = AppSpacing.lg, vertical = AppSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(maxLines = 1, overflow = TextOverflow.Ellipsis, style = AppTextStyles.listPrimary, text = title)
                Spacer(modifier = Modifier.height(AppSpacing.xxs))
                Text(maxLines = 1, overflow = TextOverflow.Ellipsis, style = AppTextStyles.listSecondary, text = subtitle)
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
                modifier = Modifier.fillMaxWidth().padding(bottom = AppSpacing.lg, end = AppSpacing.lg, start = AppSpacing.lg),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.md),
            ) {
                Spacer(modifier = Modifier.height(AppSpacing.xs))
                content()
            }
        }
    }
}
