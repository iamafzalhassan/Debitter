package com.example.debitter.ui.letter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.debitter.model.DocumentKind
import com.example.debitter.model.Letterhead
import com.example.debitter.model.RefundLetter
import com.example.debitter.model.ShippingAgent
import com.example.debitter.ui.components.AppSnackbarHost
import com.example.debitter.ui.components.AppTopBar
import com.example.debitter.ui.components.PresetSheet
import com.example.debitter.ui.components.PrimaryButton
import com.example.debitter.ui.components.SecondaryButton
import com.example.debitter.ui.components.SectionHeader
import com.example.debitter.ui.components.rememberAppSnackbarState
import com.example.debitter.ui.letter.components.AgentFields
import com.example.debitter.ui.letter.components.LetterTextPanel
import com.example.debitter.ui.letter.components.LetterheadFields
import com.example.debitter.ui.letter.components.ReferenceFields
import com.example.debitter.ui.theme.AppColors
import com.example.debitter.ui.theme.AppSpacing
import kotlinx.coroutines.launch

@Composable
fun LetterScreen(
    letterheads: List<Letterhead>,
    agents: List<ShippingAgent>,
    onBack: () -> Unit,
    onPreview: () -> Unit,
    onRecent: () -> Unit,
    onEvent: (LetterEvent) -> Unit,
    letter: RefundLetter,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val snackbarState = rememberAppSnackbarState()

    var isAgentSheetOpen by remember { mutableStateOf(false) }
    var isDocumentTextExpanded by rememberSaveable { mutableStateOf(false) }
    var isLetterheadSheetOpen by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize().imePadding(),
        containerColor = AppColors.surfaceBase,
        snackbarHost = { AppSnackbarHost(state = snackbarState) },
        topBar = {
            AppTopBar(onBack = onBack, title = DocumentKind.REFUND_LETTER.title) {
                IconButton(onClick = onRecent) {
                    Icon(contentDescription = "Recent letters", imageVector = Icons.Outlined.ReceiptLong, tint = AppColors.textSecondary)
                }
            }
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(bottom = AppSpacing.xl, top = AppSpacing.lg),
        ) {
            item(key = "document-text") {
                LetterTextPanel(
                    modifier = Modifier.padding(bottom = AppSpacing.xl, end = AppSpacing.screenPadding, start = AppSpacing.screenPadding),
                    isExpanded = isDocumentTextExpanded,
                    labels = letter.labels,
                    onLabelChange = { field, value -> onEvent(LetterEvent.SetLabel(value = value, field = field)) },
                    onToggle = { isDocumentTextExpanded = !isDocumentTextExpanded },
                )
            }
            item(key = "customer") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = AppSpacing.xl, end = AppSpacing.screenPadding, start = AppSpacing.screenPadding),
                ) {
                    SectionHeader(label = "Customer")
                    LetterheadFields(
                        letterhead = letter.letterhead,
                        onFieldChange = { field, value -> onEvent(LetterEvent.SetLetterheadField(value = value, field = field)) },
                        onPick = { isLetterheadSheetOpen = true },
                    )
                }
            }
            item(key = "shipping-agent") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = AppSpacing.xl, end = AppSpacing.screenPadding, start = AppSpacing.screenPadding),
                ) {
                    SectionHeader(label = "Shipping Agent")
                    AgentFields(
                        agent = letter.agent,
                        onFieldChange = { field, value -> onEvent(LetterEvent.SetAgentField(value = value, field = field)) },
                        onPick = { isAgentSheetOpen = true },
                    )
                }
            }
            item(key = "shipment") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = AppSpacing.lg, end = AppSpacing.screenPadding, start = AppSpacing.screenPadding),
                ) {
                    SectionHeader(label = "Shipment")
                    ReferenceFields(
                        date = letter.date,
                        labels = letter.labels,
                        onFieldChange = { field, value -> onEvent(LetterEvent.SetReferenceField(value = value, field = field)) },
                        references = letter.references,
                    )
                }
            }
            item(key = "actions") {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = AppSpacing.screenPadding),
                    horizontalArrangement = Arrangement.spacedBy(AppSpacing.md),
                ) {
                    SecondaryButton(
                        modifier = Modifier.weight(1f),
                        label = "Reset",
                        onClick = {
                            onEvent(LetterEvent.Reset)
                            scope.launch { snackbarState.showBrief("The refund letter is back to its defaults. Nothing you entered was kept.") }
                        },
                    )
                    PrimaryButton(modifier = Modifier.weight(1f), isEnabled = letter.isReady, label = "Preview", onClick = onPreview)
                }
            }
        }
    }

    if (isLetterheadSheetOpen) {
        PresetSheet(
            detail = { it.addressLine },
            items = letterheads,
            name = { it.name },
            onDismiss = { isLetterheadSheetOpen = false },
            onPick = {
                isLetterheadSheetOpen = false
                onEvent(LetterEvent.SelectLetterhead(it))
            },
            title = "Choose Customer",
        )
    }

    if (isAgentSheetOpen) {
        PresetSheet(
            detail = { it.address.lines().joinToString(", ") },
            items = agents,
            name = { it.name },
            onDismiss = { isAgentSheetOpen = false },
            onPick = {
                isAgentSheetOpen = false
                onEvent(LetterEvent.SelectAgent(it))
            },
            title = "Choose Shipping Agent",
        )
    }
}
