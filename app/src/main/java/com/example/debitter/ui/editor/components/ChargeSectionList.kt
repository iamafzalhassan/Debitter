package com.example.debitter.ui.editor.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.debitter.model.ChargeLine
import com.example.debitter.ui.components.AmountField
import com.example.debitter.ui.components.AppPlainField
import com.example.debitter.ui.components.SecondaryButton
import com.example.debitter.ui.components.SectionHeader
import com.example.debitter.ui.theme.AppSpacing
import java.math.BigDecimal

fun LazyListScope.chargeSection(
    addLabel: String,
    heading: String,
    lines: List<ChargeLine>,
    onAdd: () -> Unit,
    onAmountChange: (ChargeLine, BigDecimal?) -> Unit,
    onLabelChange: (ChargeLine, String) -> Unit,
) {
    item(key = "heading-$addLabel") {
        SectionHeader(modifier = Modifier.padding(horizontal = AppSpacing.screenPadding), label = heading)
    }
    items(items = lines, key = { it.id.toString() }) { line ->
        ChargeRow(
            modifier = Modifier.padding(bottom = AppSpacing.chargeRowGap, start = AppSpacing.screenPadding, end = AppSpacing.screenPadding),
            onAmountChange = { onAmountChange(line, it) },
            onLabelChange = { onLabelChange(line, it) },
            line = line,
        )
    }
    item(key = "action-$addLabel") {
        SecondaryButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppSpacing.screenPadding)
                .padding(top = AppSpacing.sm, bottom = AppSpacing.xl),
            label = addLabel,
            onClick = onAdd,
        )
    }
}

@Composable
private fun ChargeRow(onAmountChange: (BigDecimal?) -> Unit, onLabelChange: (String) -> Unit, line: ChargeLine, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth().height(AppSpacing.controlHeight),
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AppPlainField(modifier = Modifier.weight(1f), onValueChange = onLabelChange, value = line.label)
        AmountField(modifier = Modifier.width(AppSpacing.amountField), onValueChange = onAmountChange, value = line.amount)
    }
}
