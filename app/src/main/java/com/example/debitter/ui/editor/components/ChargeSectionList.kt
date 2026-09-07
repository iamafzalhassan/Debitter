package com.example.debitter.ui.editor.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.zIndex
import com.example.debitter.model.ChargeLine
import com.example.debitter.model.ChargeSection
import com.example.debitter.ui.components.AmountField
import com.example.debitter.ui.components.AppPlainField
import com.example.debitter.ui.components.SecondaryButton
import com.example.debitter.ui.components.SectionHeader
import com.example.debitter.ui.theme.AppColors
import com.example.debitter.ui.theme.AppSpacing
import java.math.BigDecimal
import kotlin.math.roundToInt

private const val NO_DRAG: Int = -1

@Composable
fun ChargeSectionList(
    addLabel: String,
    heading: String,
    lines: List<ChargeLine>,
    onAdd: () -> Unit,
    onAmountChange: (ChargeLine, BigDecimal?) -> Unit,
    onLabelChange: (ChargeLine, String) -> Unit,
    onMove: (Int, Int) -> Unit,
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

                key(line.id) {
                    ChargeRow(
                        modifier = Modifier
                            .zIndex(if (isDragging) 1f else 0f)
                            .graphicsLayer { translationY = if (isDragging) dragOffset else shift },
                        dragModifier = Modifier.pointerInput(index, lines.size) {
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
private fun ChargeRow(
    isDragging: Boolean,
    dragModifier: Modifier,
    onAmountChange: (BigDecimal?) -> Unit,
    onLabelChange: (String) -> Unit,
    line: ChargeLine,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth().height(AppSpacing.controlHeight),
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = dragModifier.width(AppSpacing.dragHandle).height(AppSpacing.controlHeight),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                modifier = Modifier.size(AppSpacing.iconTile),
                contentDescription = "Reorder",
                imageVector = Icons.Filled.Menu,
                tint = if (isDragging) AppColors.primary else AppColors.textTertiary,
            )
        }
        AppPlainField(modifier = Modifier.weight(1f), onValueChange = onLabelChange, value = line.label)
        AmountField(modifier = Modifier.width(AppSpacing.amountField), onValueChange = onAmountChange, value = line.amount)
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
