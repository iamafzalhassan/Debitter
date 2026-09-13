package com.example.debitter.ui.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.example.debitter.ui.components.DangerButton
import com.example.debitter.ui.components.PrimaryButton
import com.example.debitter.ui.components.SheetActions
import com.example.debitter.ui.components.SheetFrame
import com.example.debitter.ui.theme.AppColors
import com.example.debitter.ui.theme.AppSpacing
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntrySheet(isNew: Boolean, isValid: Boolean, noun: String, onDelete: () -> Unit, onDismiss: () -> Unit, onSave: () -> Unit, content: @Composable ColumnScope.() -> Unit) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val hideThen: (() -> Unit) -> Unit = { action -> scope.launch { sheetState.hide() }.invokeOnCompletion { action() } }

    ModalBottomSheet(containerColor = AppColors.surfaceCard, onDismissRequest = onDismiss, sheetState = sheetState) {
        SheetFrame(modifier = Modifier.imePadding(), title = if (isNew) "Add $noun" else "Edit $noun") {
            Column(
                modifier = Modifier.weight(weight = 1f, fill = false).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.md),
            ) {
                content()
            }
            Spacer(modifier = Modifier.height(AppSpacing.lg))
            SheetActions(
                primary = {
                    PrimaryButton(
                        modifier = Modifier.weight(1f),
                        isEnabled = isValid,
                        label = if (isNew) "Add" else "Save",
                        onClick = { hideThen(onSave) },
                    )
                },
                secondary = {
                    DangerButton(
                        modifier = Modifier.weight(1f),
                        label = if (isNew) "Cancel" else "Delete",
                        onClick = { hideThen(if (isNew) onDismiss else onDelete) },
                    )
                },
            )
        }
    }
}
