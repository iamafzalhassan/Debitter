package com.example.debitter.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.example.debitter.ui.theme.AppColors
import com.example.debitter.ui.theme.AppSpacing
import com.example.debitter.ui.theme.AppTextStyles
import java.util.Locale

@Composable
fun AppTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    textAlign: TextAlign = TextAlign.Start,
) {
    val focusManager = LocalFocusManager.current

    FieldFrame(modifier = modifier, label = label, textAlign = textAlign) {
        BasicTextField(
            modifier = Modifier.fillMaxWidth(),
            cursorBrush = SolidColor(AppColors.primary),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Characters,
                imeAction = if (singleLine) ImeAction.Done else ImeAction.Default,
                keyboardType = keyboardType,
            ),
            onValueChange = { onValueChange(it.uppercase(Locale.ROOT)) },
            singleLine = singleLine,
            textStyle = AppTextStyles.fieldValue.copy(textAlign = textAlign),
            value = value,
        )
    }
}

@Composable
fun AppStaticField(label: String, value: String, modifier: Modifier = Modifier) {
    FieldFrame(modifier = modifier, label = label, textAlign = TextAlign.Start) {
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
fun AppPlainField(value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier) {
    val focusManager = LocalFocusManager.current
    val shape = RoundedCornerShape(AppSpacing.radiusField)

    Box(
        modifier = modifier
            .height(AppSpacing.controlHeight)
            .background(color = AppColors.surfaceCard, shape = shape)
            .border(border = BorderStroke(AppSpacing.hairline, AppColors.divider), shape = shape)
            .padding(horizontal = AppSpacing.md),
        contentAlignment = Alignment.CenterStart,
    ) {
        BasicTextField(
            modifier = Modifier.fillMaxWidth(),
            cursorBrush = SolidColor(AppColors.primary),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters, imeAction = ImeAction.Done),
            onValueChange = { onValueChange(it.uppercase(Locale.ROOT)) },
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

