package com.example.debitter.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import com.example.debitter.ui.theme.AppColors
import com.example.debitter.ui.theme.AppSpacing
import com.example.debitter.ui.theme.AppTextStyles
import com.example.debitter.util.MoneyFormat
import java.math.BigDecimal

@Composable
fun AmountField(
    onValueChange: (BigDecimal?) -> Unit,
    value: BigDecimal?,
    modifier: Modifier = Modifier,
    isAutoFocused: Boolean = false,
    onSubmit: () -> Unit = {},
) {
    val focusRequester = remember { FocusRequester() }

    var field by remember { mutableStateOf(TextFieldValue(text = value?.toPlainString().orEmpty())) }

    LaunchedEffect(isAutoFocused) {
        if (isAutoFocused) focusRequester.requestFocus()
    }

    Box(
        modifier = modifier
            .height(AppSpacing.controlHeight)
            .clip(RoundedCornerShape(AppSpacing.radiusField))
            .background(AppColors.surfaceField)
            .padding(horizontal = AppSpacing.md),
        contentAlignment = Alignment.CenterEnd,
    ) {
        BasicTextField(
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .onFocusChanged { state -> if (state.isFocused) field = field.copy(selection = TextRange(0, field.text.length)) },
            cursorBrush = SolidColor(AppColors.primary),
            keyboardActions = KeyboardActions(onDone = { onSubmit() }),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Decimal),
            onValueChange = { input ->
                val cleaned = MoneyFormat.sanitize(input.text)

                field = if (cleaned == input.text) input else TextFieldValue(text = cleaned, selection = TextRange(cleaned.length))
                onValueChange(MoneyFormat.parse(cleaned))
            },
            singleLine = true,
            textStyle = AppTextStyles.amount.copy(textAlign = TextAlign.End),
            value = field,
        )
    }
}
