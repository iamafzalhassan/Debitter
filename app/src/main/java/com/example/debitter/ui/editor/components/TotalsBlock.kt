package com.example.debitter.ui.editor.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import com.example.debitter.ui.components.DottedDivider
import com.example.debitter.ui.components.SecondaryButton
import com.example.debitter.ui.theme.AppColors
import com.example.debitter.ui.theme.AppSpacing
import com.example.debitter.ui.theme.AppTextStyles
import com.example.debitter.util.MoneyFormat
import java.math.BigDecimal

@Composable
fun TotalsBlock(
    hasCharges: Boolean,
    showsAdvance: Boolean,
    advanceLabel: String,
    subTotalLabel: String,
    totalLabel: String,
    onAdvanceTap: () -> Unit,
    advance: BigDecimal?,
    subTotal: BigDecimal,
    total: BigDecimal,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AppSpacing.radiusCard))
            .background(AppColors.surfaceField)
            .padding(horizontal = AppSpacing.lg, vertical = AppSpacing.md),
    ) {
        TotalsLine(isEditable = false, isStrong = false, label = subTotalLabel, value = MoneyFormat.format(subTotal))
        if (showsAdvance) {
            DottedDivider()
            TotalsLine(
                modifier = Modifier.clickable(indication = null, interactionSource = remember { MutableInteractionSource() }, onClick = onAdvanceTap),
                isEditable = true,
                isStrong = false,
                label = advanceLabel,
                value = MoneyFormat.format(advance ?: MoneyFormat.zero),
            )
        }
        DottedDivider()
        TotalsLine(isEditable = false, isStrong = true, label = totalLabel, value = MoneyFormat.format(total))
        if (!showsAdvance && hasCharges) {
            Spacer(modifier = Modifier.height(AppSpacing.sm))
            SecondaryButton(modifier = Modifier.fillMaxWidth(), label = "Add Advance", onClick = onAdvanceTap)
            Spacer(modifier = Modifier.height(AppSpacing.xs))
        }
    }
}

@Composable
private fun TotalsLine(isEditable: Boolean, isStrong: Boolean, label: String, value: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth().padding(vertical = AppSpacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(maxLines = 1, overflow = TextOverflow.Ellipsis, style = AppTextStyles.overline, text = label)
        if (isEditable) {
            Spacer(modifier = Modifier.size(AppSpacing.xs))
            Icon(
                modifier = Modifier.size(AppSpacing.iconInline),
                contentDescription = null,
                imageVector = Icons.Outlined.Edit,
                tint = AppColors.textSecondary,
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Text(
            maxLines = 1,
            style = if (isStrong) AppTextStyles.totalsValueBold else AppTextStyles.totalsValue,
            text = value,
        )
    }
}
