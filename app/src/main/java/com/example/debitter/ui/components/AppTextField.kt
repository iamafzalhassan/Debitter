package com.example.debitter.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.example.debitter.ui.theme.AppColors
import com.example.debitter.ui.theme.AppSpacing
import com.example.debitter.ui.theme.AppTextStyles

@Composable
fun AppTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    capitalization: KeyboardCapitalization = KeyboardCapitalization.Characters,
    keyboardType: KeyboardType = KeyboardType.Text,
    textAlign: TextAlign = TextAlign.Start,
) {
    FieldFrame(label = label, modifier = modifier, textAlign = textAlign) {
        BasicTextField(
            modifier = Modifier.fillMaxWidth(),
            cursorBrush = SolidColor(AppColors.primary),
            keyboardOptions = KeyboardOptions(
                capitalization = capitalization,
                imeAction = if (singleLine) ImeAction.Next else ImeAction.Default,
                keyboardType = keyboardType,
            ),
            onValueChange = onValueChange,
            singleLine = singleLine,
            textStyle = AppTextStyles.fieldValue.copy(textAlign = textAlign),
            value = value,
        )
    }
}

@Composable
fun AppDisplayField(label: String, value: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    FieldFrame(label = label, modifier = modifier.clickable(onClick = onClick), textAlign = TextAlign.Start) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = AppTextStyles.fieldValue,
            text = value,
        )
    }
}

@Composable
fun AppPlainField(onValueChange: (String) -> Unit, value: String, modifier: Modifier = Modifier, capitalization: KeyboardCapitalization = KeyboardCapitalization.Words) {
    Box(
        modifier = modifier
            .height(AppSpacing.controlHeight)
            .background(color = AppColors.surfaceField, shape = RoundedCornerShape(AppSpacing.radiusField))
            .padding(horizontal = AppSpacing.md),
        contentAlignment = Alignment.CenterStart,
    ) {
        BasicTextField(
            modifier = Modifier.fillMaxWidth(),
            cursorBrush = SolidColor(AppColors.primary),
            keyboardOptions = KeyboardOptions(capitalization = capitalization, imeAction = ImeAction.Next),
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = AppTextStyles.fieldValue,
            value = value,
        )
    }
}

@Composable
private fun FieldFrame(label: String, textAlign: TextAlign, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = AppSpacing.controlHeight)
            .background(color = AppColors.surfaceField, shape = RoundedCornerShape(AppSpacing.radiusField))
            .padding(horizontal = AppSpacing.md, vertical = AppSpacing.sm),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = AppTextStyles.fieldLabel.copy(textAlign = textAlign),
            text = label,
        )
        content()
    }
}

