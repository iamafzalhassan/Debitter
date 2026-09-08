package com.example.debitter.ui.editor.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.debitter.model.ChargeLine
import com.example.debitter.ui.components.AmountField
import com.example.debitter.ui.components.AppPlainField
import com.example.debitter.ui.components.SecondaryButton
import com.example.debitter.ui.components.SectionHeader
import com.example.debitter.ui.theme.AppSpacing
import java.math.BigDecimal

@Composable
fun ChargeSectionList(
    addLabel: String,
    heading: String,
    lines: List<ChargeLine>,
    onAdd: () -> Unit,
    onAmountChange: (ChargeLine, BigDecimal?) -> Unit,
    onLabelChange: (ChargeLine, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        SectionHeader(label = heading)
        Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.chargeRowGap)) {
            for (line in lines) {
                key(line.id) {
                    ChargeRow(
                        onAmountChange = { onAmountChange(line, it) },
                        onLabelChange = { onLabelChange(line, it) },
                        line = line,
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(AppSpacing.md))
        SecondaryButton(modifier = Modifier.fillMaxWidth(), label = addLabel, onClick = onAdd)
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
