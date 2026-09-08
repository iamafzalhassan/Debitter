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
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
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
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    val transformation = remember { ThousandsTransformation() }

    var isFocused by remember { mutableStateOf(false) }

    var field by remember { mutableStateOf(TextFieldValue(text = MoneyFormat.plain(value))) }

    LaunchedEffect(isAutoFocused) {
        if (!isAutoFocused) return@LaunchedEffect
        withFrameNanos {}
        focusRequester.requestFocus()
    }

    LaunchedEffect(isFocused) {
        if (isFocused) field = field.copy(selection = TextRange(0, field.text.length))
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
                .onFocusChanged { state -> isFocused = state.isFocused },
            cursorBrush = SolidColor(AppColors.primary),
            keyboardActions = KeyboardActions(onDone = {
                onSubmit()
                focusManager.clearFocus()
            }),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Decimal),
            onValueChange = { input ->
                val cleaned = MoneyFormat.sanitize(input.text)

                field = if (cleaned == input.text) input else TextFieldValue(selection = TextRange(cleaned.length), text = cleaned)
                onValueChange(MoneyFormat.parse(cleaned))
            },
            singleLine = true,
            textStyle = AppTextStyles.amount.copy(textAlign = TextAlign.End),
            value = field,
            visualTransformation = transformation,
        )
    }
}

private class ThousandsTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val raw = text.text
        val dot = raw.indexOf('.')
        val wholeLength = if (dot < 0) raw.length else dot
        val grouped = StringBuilder()
        val forward = IntArray(raw.length + 1)

        for (index in raw.indices) {
            forward[index] = grouped.length
            grouped.append(raw[index])
            if (index < wholeLength - 1 && (wholeLength - 1 - index) % MoneyFormat.GROUP_SIZE == 0) grouped.append(MoneyFormat.SEPARATOR)
        }
        forward[raw.length] = grouped.length

        val backward = IntArray(grouped.length + 1)
        var original = 0

        for (offset in 0..grouped.length) {
            while (original < raw.length && forward[original + 1] <= offset) original++
            backward[offset] = original
        }

        val mapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int = forward[offset.coerceIn(0, raw.length)]

            override fun transformedToOriginal(offset: Int): Int = backward[offset.coerceIn(0, grouped.length)]
        }

        return TransformedText(offsetMapping = mapping, text = AnnotatedString(grouped.toString()))
    }
}
