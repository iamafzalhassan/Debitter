package com.example.debitter.ui.editor.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.example.debitter.ui.components.DottedDivider
import com.example.debitter.ui.components.PrimaryButton
import com.example.debitter.ui.components.SecondaryButton
import com.example.debitter.ui.theme.AppColors
import com.example.debitter.ui.theme.AppSpacing
import com.example.debitter.ui.theme.AppTextStyles
import com.example.debitter.util.MoneyFormat
import java.math.BigDecimal

@Composable
fun TotalsFooter(
    showsActions: Boolean,
    advanceLabel: String,
    subTotalLabel: String,
    totalLabel: String,
    onAdvanceChange: (BigDecimal?) -> Unit,
    onPreview: () -> Unit,
    onReset: () -> Unit,
    advance: BigDecimal?,
    subTotal: BigDecimal,
    total: BigDecimal,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(AppColors.surfaceBase)
            .padding(horizontal = AppSpacing.screenPadding, vertical = AppSpacing.md),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(AppSpacing.radiusCard))
                .background(AppColors.surfaceField)
                .padding(horizontal = AppSpacing.lg, vertical = AppSpacing.sm),
        ) {
            TotalsLine(label = subTotalLabel, value = MoneyFormat.format(subTotal))
            DottedDivider()
            AdvanceLine(advance = advance, label = advanceLabel, onAdvanceChange = onAdvanceChange)
            DottedDivider()
            TotalsLine(isStrong = true, label = totalLabel, value = MoneyFormat.format(total))
        }
        if (showsActions) {
            Spacer(modifier = Modifier.height(AppSpacing.md))
            Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.md), modifier = Modifier.fillMaxWidth()) {
                SecondaryButton(modifier = Modifier.weight(1f), label = "Reset", onClick = onReset)
                PrimaryButton(modifier = Modifier.weight(1f), label = "Preview", onClick = onPreview)
            }
        }
    }
}

@Composable
private fun TotalsLine(label: String, value: String, modifier: Modifier = Modifier, isStrong: Boolean = false) {
    Row(
        modifier = modifier.fillMaxWidth().height(AppSpacing.totalsRowHeight),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(maxLines = 1, overflow = TextOverflow.Ellipsis, style = AppTextStyles.overline, text = label.uppercase())
        Spacer(modifier = Modifier.weight(1f))
        Text(
            maxLines = 1,
            style = if (isStrong) AppTextStyles.totalsValueBold else AppTextStyles.totalsValue,
            text = value,
        )
    }
}

@Composable
private fun AdvanceLine(label: String, onAdvanceChange: (BigDecimal?) -> Unit, advance: BigDecimal?, modifier: Modifier = Modifier) {
    var text by remember(advance == null) { mutableStateOf(advance?.toPlainString().orEmpty()) }

    Row(
        modifier = modifier.fillMaxWidth().height(AppSpacing.totalsRowHeight),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(maxLines = 1, overflow = TextOverflow.Ellipsis, style = AppTextStyles.overline, text = label.uppercase())
        Spacer(modifier = Modifier.weight(1f))
        Box(
            modifier = Modifier
                .width(AppSpacing.amountColumn)
                .clip(RoundedCornerShape(AppSpacing.radiusField))
                .background(AppColors.surfaceCard)
                .padding(horizontal = AppSpacing.sm, vertical = AppSpacing.xs),
        ) {
            BasicTextField(
                modifier = Modifier.fillMaxWidth(),
                cursorBrush = SolidColor(AppColors.primary),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                onValueChange = { raw ->
                    val cleaned = MoneyFormat.sanitize(raw)
                    text = cleaned
                    onAdvanceChange(MoneyFormat.parse(cleaned))
                },
                singleLine = true,
                textStyle = AppTextStyles.totalsValue.copy(textAlign = TextAlign.End),
                value = text,
            )
        }
    }
}
