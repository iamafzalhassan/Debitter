package com.example.debitter.ui.editor.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import com.example.debitter.model.ShipmentType
import com.example.debitter.ui.theme.AppColors
import com.example.debitter.ui.theme.AppSpacing
import com.example.debitter.ui.theme.AppTextStyles

@Composable
fun ShipmentTypeSelector(onSelect: (ShipmentType) -> Unit, selected: ShipmentType, modifier: Modifier = Modifier) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = AppSpacing.screenPadding),
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
    ) {
        items(items = ShipmentType.entries, key = { it.name }) { type ->
            ShipmentChip(isSelected = type == selected, label = type.label, onClick = { onSelect(type) })
        }
    }
}

@Composable
private fun ShipmentChip(isSelected: Boolean, label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(AppSpacing.radiusButton)

    Box(
        modifier = modifier
            .height(AppSpacing.touchTarget)
            .clip(shape)
            .background(if (isSelected) AppColors.primary else AppColors.surfaceCard)
            .border(border = BorderStroke(AppSpacing.hairline, if (isSelected) AppColors.primary else AppColors.divider), shape = shape)
            .clickable(onClick = onClick)
            .padding(horizontal = AppSpacing.lg),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            color = if (isSelected) AppColors.primaryOn else AppColors.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = AppTextStyles.listPrimary,
            text = label,
        )
    }
}
