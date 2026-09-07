package com.example.debitter.ui.editor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.example.debitter.model.ChargeSection
import com.example.debitter.ui.components.AppSnackbarHost
import com.example.debitter.ui.components.PrimaryButton
import com.example.debitter.ui.components.SecondaryButton
import com.example.debitter.ui.components.SectionHeader
import com.example.debitter.ui.components.rememberAppSnackbarState
import com.example.debitter.ui.editor.components.AdvanceSheet
import com.example.debitter.ui.editor.components.ChargeSectionList
import com.example.debitter.ui.editor.components.DocumentTextPanel
import com.example.debitter.ui.editor.components.HeaderFields
import com.example.debitter.ui.editor.components.ShipmentTypeSelector
import com.example.debitter.ui.editor.components.TotalsBlock
import com.example.debitter.ui.theme.AppColors
import com.example.debitter.ui.theme.AppSpacing
import com.example.debitter.ui.theme.AppTextStyles
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(state: EditorState, onEvent: (EditorEvent) -> Unit, onPreview: () -> Unit, modifier: Modifier = Modifier) {
    val note = state.note
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val snackbarState = rememberAppSnackbarState()

    var isAdvanceSheetOpen by remember { mutableStateOf(false) }
    var isDocumentTextExpanded by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize().imePadding(),
        containerColor = AppColors.surfaceBase,
        snackbarHost = { AppSnackbarHost(state = snackbarState) },
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AppColors.surfaceBase, scrolledContainerColor = AppColors.surfaceBase),
                title = { Text(maxLines = 1, overflow = TextOverflow.Ellipsis, style = AppTextStyles.screenTitle, text = "Debit Note") },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.xl),
        ) {
            Spacer(modifier = Modifier.height(AppSpacing.xs))
            ShipmentTypeSelector(onSelect = { onEvent(EditorEvent.SetShipmentType(it)) }, selected = state.shipmentType)
            DocumentTextPanel(
                modifier = Modifier.padding(horizontal = AppSpacing.screenPadding),
                company = note.company,
                isExpanded = isDocumentTextExpanded,
                labels = note.labels,
                onCompanyChange = { field, value -> onEvent(EditorEvent.SetCompanyField(value = value, field = field)) },
                onLabelChange = { field, value -> onEvent(EditorEvent.SetLabel(value = value, field = field)) },
                onToggle = { isDocumentTextExpanded = !isDocumentTextExpanded },
            )
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = AppSpacing.screenPadding)) {
                SectionHeader(label = "Shipment")
                HeaderFields(
                    header = note.header,
                    labels = note.labels,
                    onDateChange = { onEvent(EditorEvent.SetDate(it)) },
                    onFieldChange = { field, value -> onEvent(EditorEvent.SetHeaderField(value = value, field = field)) },
                )
            }
            ChargeSectionList(
                modifier = Modifier.padding(horizontal = AppSpacing.screenPadding),
                addLabel = "Add Statutory Expense",
                heading = note.labels.statutorySection,
                lines = note.statutory,
                onAdd = { onEvent(EditorEvent.AddCharge(ChargeSection.STATUTORY)) },
                onAmountChange = { line, amount -> onEvent(EditorEvent.SetChargeAmount(amount = amount, section = ChargeSection.STATUTORY, id = line.id)) },
                onLabelChange = { line, label -> onEvent(EditorEvent.SetChargeLabel(label = label, section = ChargeSection.STATUTORY, id = line.id)) },
                onMove = { from, to -> onEvent(EditorEvent.MoveCharge(from = from, to = to, section = ChargeSection.STATUTORY)) },
                section = ChargeSection.STATUTORY,
            )
            ChargeSectionList(
                modifier = Modifier.padding(horizontal = AppSpacing.screenPadding),
                addLabel = "Add Other Expense",
                heading = note.labels.otherSection,
                lines = note.other,
                onAdd = { onEvent(EditorEvent.AddCharge(ChargeSection.OTHER)) },
                onAmountChange = { line, amount -> onEvent(EditorEvent.SetChargeAmount(amount = amount, section = ChargeSection.OTHER, id = line.id)) },
                onLabelChange = { line, label -> onEvent(EditorEvent.SetChargeLabel(label = label, section = ChargeSection.OTHER, id = line.id)) },
                onMove = { from, to -> onEvent(EditorEvent.MoveCharge(from = from, to = to, section = ChargeSection.OTHER)) },
                section = ChargeSection.OTHER,
            )
            TotalsBlock(
                modifier = Modifier.padding(horizontal = AppSpacing.screenPadding),
                advance = note.advanceReceived,
                advanceLabel = note.labels.advanceReceived,
                hasCharges = note.hasCharges,
                onAdvanceTap = { isAdvanceSheetOpen = true },
                showsAdvance = note.showsAdvance,
                subTotal = note.subTotal,
                subTotalLabel = note.labels.subTotal,
                total = note.total,
                totalLabel = note.labels.total,
            )
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = AppSpacing.screenPadding),
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.md),
            ) {
                SecondaryButton(
                    modifier = Modifier.weight(1f),
                    label = "Reset",
                    onClick = {
                        onEvent(EditorEvent.Reset)
                        scope.launch { snackbarState.showBrief("The note is back to its defaults. Nothing you entered was kept.") }
                    },
                )
                PrimaryButton(modifier = Modifier.weight(1f), isEnabled = note.hasCharges, label = "Preview", onClick = onPreview)
            }
            Spacer(modifier = Modifier.height(AppSpacing.sm))
        }
    }

    if (isAdvanceSheetOpen) {
        AdvanceSheet(
            advance = note.advanceReceived,
            onDismiss = { isAdvanceSheetOpen = false },
            onRemove = {
                onEvent(EditorEvent.SetAdvance(null))
                isAdvanceSheetOpen = false
            },
            onSave = {
                onEvent(EditorEvent.SetAdvance(it))
                isAdvanceSheetOpen = false
            },
            subTotal = note.subTotal,
        )
    }
}
