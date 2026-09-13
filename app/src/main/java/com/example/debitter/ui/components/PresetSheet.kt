package com.example.debitter.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.example.debitter.ui.theme.AppColors
import com.example.debitter.ui.theme.AppSpacing
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
