package com.example.debitter.ui.letter.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import com.example.debitter.ui.components.SheetFrame
import com.example.debitter.ui.theme.AppColors
import com.example.debitter.ui.theme.AppSpacing
import com.example.debitter.ui.theme.AppTextStyles
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> PresetSheet(title: String, items: List<T>, onDismiss: () -> Unit, detail: (T) -> String, name: (T) -> String, onPick: (T) -> Unit) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val hideThen: (() -> Unit) -> Unit = { action -> scope.launch { sheetState.hide() }.invokeOnCompletion { action() } }

    ModalBottomSheet(containerColor = AppColors.surfaceCard, onDismissRequest = onDismiss, sheetState = sheetState) {
        SheetFrame(title = title) {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
                items(items = items) { item ->
                    PresetTile(detail = detail(item), name = name(item), onClick = { hideThen({ onPick(item) }) })
                }
            }
        }
    }
}

@Composable
private fun PresetTile(detail: String, name: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(AppSpacing.radiusCard)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(AppColors.surfaceCard)
            .border(border = BorderStroke(AppSpacing.hairline, AppColors.divider), shape = shape)
            .clickable(onClick = onClick)
            .padding(AppSpacing.cardPadding),
    ) {
        Text(maxLines = 1, overflow = TextOverflow.Ellipsis, style = AppTextStyles.listPrimary, text = name)
        Spacer(modifier = Modifier.height(AppSpacing.xs))
        Text(maxLines = 1, overflow = TextOverflow.Ellipsis, style = AppTextStyles.listSecondary, text = detail)
    }
}
