package com.example.debitter.ui.editor.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.zIndex
import com.example.debitter.model.ChargeLine
import com.example.debitter.model.ChargeSection
import com.example.debitter.ui.components.SecondaryButton
import com.example.debitter.ui.components.SectionHeader
import com.example.debitter.ui.theme.AppColors
import com.example.debitter.ui.theme.AppSpacing
import com.example.debitter.ui.theme.AppTextStyles
import com.example.debitter.util.MoneyFormat
import java.math.BigDecimal
import kotlin.math.roundToInt

private const val NO_DRAG: Int = -1

@Composable
fun ChargeSectionList(
    heading: String,
    lines: List<ChargeLine>,
    onAdd: () -> Unit,
    onAmountChange: (ChargeLine, BigDecimal?) -> Unit,
    onLabelChange: (ChargeLine, String) -> Unit,
    onMove: (Int, Int) -> Unit,
    onRemove: (ChargeLine) -> Unit,
    section: ChargeSection,
    modifier: Modifier = Modifier,
) {
    val haptics = LocalHapticFeedback.current
    val stride = with(LocalDensity.current) { AppSpacing.chargeRowStride.toPx() }

    var dragIndex by remember(section) { mutableIntStateOf(NO_DRAG) }
    var dragOffset by remember(section) { mutableFloatStateOf(0f) }

    val target = targetIndex(dragIndex, dragOffset, stride, lines.size)

    Column(modifier = modifier.fillMaxWidth()) {
        SectionHeader(label = heading)
        Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.chargeRowGap)) {
            lines.forEachIndexed { index, line ->
                val isDragging = index == dragIndex
                val shift by animateFloatAsState(targetValue = shiftFor(index, dragIndex, target, stride), label = "shift")

                ChargeRow(
                    modifier = Modifier
                        .zIndex(if (isDragging) 1f else 0f)
                        .graphicsLayer { translationY = if (isDragging) dragOffset else shift }
                        .pointerInput(index, lines.size) {
                            detectDragGesturesAfterLongPress(
                                onDragStart = {
                                    dragIndex = index
                                    dragOffset = 0f
                                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                },
                                onDragCancel = {
                                    dragIndex = NO_DRAG
                                    dragOffset = 0f
                                },
                                onDragEnd = {
                                    val to = targetIndex(dragIndex, dragOffset, stride, lines.size)
                                    if (dragIndex != NO_DRAG && to != dragIndex) onMove(dragIndex, to)
                                    dragIndex = NO_DRAG
                                    dragOffset = 0f
                                },
                                onDrag = { change, amount ->
                                    change.consume()
                                    dragOffset += amount.y
                                },
                            )
                        },
                    isDragging = isDragging,
                    line = line,
                    onAmountChange = { onAmountChange(line, it) },
                    onLabelChange = { onLabelChange(line, it) },
                    onRemove = { onRemove(line) },
                )
            }
        }
        Spacer(modifier = Modifier.height(AppSpacing.md))
        SecondaryButton(modifier = Modifier.fillMaxWidth(), label = "Add row", onClick = onAdd)
    }
}

@Composable
private fun ChargeRow(
    isDragging: Boolean,
    onAmountChange: (BigDecimal?) -> Unit,
    onLabelChange: (String) -> Unit,
    onRemove: () -> Unit,
    line: ChargeLine,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(AppSpacing.radiusCard)

    var amountText by remember(line.id) { mutableStateOf(line.amount?.toPlainString().orEmpty()) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(AppSpacing.chargeRowHeight)
            .clip(shape)
            .background(AppColors.surfaceCard)
            .border(border = BorderStroke(AppSpacing.hairline, if (isDragging) AppColors.primary else AppColors.divider), shape = shape)
            .padding(horizontal = AppSpacing.cardPadding, vertical = AppSpacing.sm),
        verticalArrangement = Arrangement.SpaceEvenly,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            BasicTextField(
                modifier = Modifier.weight(1f),
                cursorBrush = SolidColor(AppColors.primary),
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                onValueChange = onLabelChange,
                singleLine = true,
                textStyle = AppTextStyles.listPrimary,
                value = line.label,
            )
            IconButton(modifier = Modifier.size(AppSpacing.iconButtonSize), onClick = onRemove) {
                Icon(
                    contentDescription = "Delete row",
                    imageVector = Icons.Filled.Close,
                    modifier = Modifier.size(AppSpacing.iconButton),
                    tint = AppColors.textTertiary,
                )
            }
        }
        Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .width(AppSpacing.amountColumn)
                    .clip(RoundedCornerShape(AppSpacing.radiusField))
                    .background(AppColors.surfaceField)
                    .padding(horizontal = AppSpacing.sm, vertical = AppSpacing.xs),
            ) {
                BasicTextField(
                    modifier = Modifier.fillMaxWidth(),
                    cursorBrush = SolidColor(AppColors.primary),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    onValueChange = { raw ->
                        val cleaned = MoneyFormat.sanitize(raw)
                        amountText = cleaned
                        onAmountChange(MoneyFormat.parse(cleaned))
                    },
                    singleLine = true,
                    textStyle = AppTextStyles.amount.copy(textAlign = TextAlign.End),
                    value = amountText,
                )
            }
        }
    }
}

private fun shiftFor(index: Int, dragIndex: Int, target: Int, stride: Float): Float = when {
    dragIndex == NO_DRAG || index == dragIndex -> 0f
    dragIndex < target && index in (dragIndex + 1)..target -> -stride
    dragIndex > target && index in target until dragIndex -> stride
    else -> 0f
}

private fun targetIndex(dragIndex: Int, dragOffset: Float, stride: Float, count: Int): Int {
    if (dragIndex == NO_DRAG || count == 0) return dragIndex
    return (dragIndex + (dragOffset / stride).roundToInt()).coerceIn(0, count - 1)
}
