package com.example.debitter.ui.letter.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import com.example.debitter.model.AgentField
import com.example.debitter.model.ShippingAgent
import com.example.debitter.model.value
import com.example.debitter.ui.components.AppTextField
import com.example.debitter.ui.components.SecondaryButton
import com.example.debitter.ui.theme.AppSpacing

@Composable
fun AgentFields(onPick: () -> Unit, onFieldChange: (AgentField, String) -> Unit, agent: ShippingAgent, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(AppSpacing.md)) {
        SecondaryButton(modifier = Modifier.fillMaxWidth(), label = if (agent.name.isBlank()) "Choose Shipping Agent" else "Change Shipping Agent", onClick = onPick)
        for (field in AgentField.entries) {
            AppTextField(
                capitalization = KeyboardCapitalization.Words,
                label = field.caption,
                onValueChange = { onFieldChange(field, it) },
                singleLine = !field.isMultiline,
                value = agent.value(field),
            )
        }
    }
}
