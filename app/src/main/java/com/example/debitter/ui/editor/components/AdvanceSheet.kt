package com.example.debitter.ui.editor.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.debitter.ui.components.AmountField
import com.example.debitter.ui.components.DangerButton
import com.example.debitter.ui.components.PrimaryButton
import com.example.debitter.ui.components.SheetActions
import com.example.debitter.ui.components.SheetFrame
import com.example.debitter.ui.components.rememberHideThen
import com.example.debitter.ui.theme.AppColors
import com.example.debitter.ui.theme.AppSpacing
import com.example.debitter.ui.theme.AppTextStyles
import com.example.debitter.util.MoneyFormat
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvanceSheet(onDismiss: () -> Unit, onRemove: () -> Unit, onSave: (BigDecimal) -> Unit, subTotal: BigDecimal, advance: BigDecimal?) {
    val sheetState = rememberModalBottomSheetState()
    val hideThen = rememberHideThen(sheetState)

    var draft by remember { mutableStateOf(advance) }

    val amount = draft ?: MoneyFormat.zero
    val isOverSubTotal = amount > subTotal
    val isValid = amount.signum() > 0 && !isOverSubTotal

    ModalBottomSheet(
        containerColor = AppColors.surfaceCard,
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        SheetFrame(modifier = Modifier.imePadding(), title = if (advance == null) "Add Advance" else "Edit Advance") {
            AmountField(
                modifier = Modifier.fillMaxWidth(),
                isAutoFocused = true,
                label = "Advance",
                onSubmit = { if (isValid) hideThen({ onSave(amount) }) },
                onValueChange = { draft = it },
                value = advance,
            )
            if (isOverSubTotal) {
                Spacer(modifier = Modifier.height(AppSpacing.sm))
                Text(style = AppTextStyles.errorHint, text = "Advance cannot exceed the sub total of ${MoneyFormat.format(subTotal)}")
            }
            Spacer(modifier = Modifier.height(AppSpacing.lg))
            SheetActions(
                primary = {
                    PrimaryButton(
                        modifier = Modifier.weight(1f),
                        isEnabled = isValid,
                        label = if (advance == null) "Add Advance" else "Save Advance",
                        onClick = { hideThen({ onSave(amount) }) },
                    )
                },
                secondary = {
                    DangerButton(
                        modifier = Modifier.weight(1f),
                        label = if (advance == null) "Cancel" else "Remove",
                        onClick = { hideThen(if (advance == null) onDismiss else onRemove) },
                    )
                },
            )
        }
    }
}
