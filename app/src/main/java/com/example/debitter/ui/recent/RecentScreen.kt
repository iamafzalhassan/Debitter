package com.example.debitter.ui.recent

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.example.debitter.model.DocumentKind
import com.example.debitter.model.SavedDocument
import com.example.debitter.pdf.PdfExporter
import com.example.debitter.pdf.SaveLocation
import com.example.debitter.ui.components.AppSnackbarHost
import com.example.debitter.ui.components.rememberAppSnackbarState
import com.example.debitter.ui.recent.components.RecentActionsSheet
import com.example.debitter.ui.recent.components.RecentTile
import com.example.debitter.ui.theme.AppColors
import com.example.debitter.ui.theme.AppSpacing
import com.example.debitter.ui.theme.AppTextStyles
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecentScreen(message: String?, onBack: () -> Unit, onMessageShown: () -> Unit, onDelete: (SavedDocument) -> Unit, onEdit: (SavedDocument) -> Unit, kind: DocumentKind, state: RecentState, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val entries: List<SavedDocument> = when (kind) {
        DocumentKind.DEBIT_NOTE -> state.notes
        DocumentKind.REFUND_LETTER -> state.letters
    }
    val exporter = remember(context) { PdfExporter(context) }
    val scope = rememberCoroutineScope()
    val snackbarState = rememberAppSnackbarState()

    var selected by remember { mutableStateOf<SavedDocument?>(null) }

    LaunchedEffect(message) {
        if (message == null) return@LaunchedEffect
        onMessageShown()
        scope.launch { snackbarState.showSuccess(message) }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = AppColors.surfaceBase,
        snackbarHost = { AppSnackbarHost(state = snackbarState) },
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AppColors.surfaceBase, scrolledContainerColor = AppColors.surfaceBase),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(contentDescription = "Back", imageVector = Icons.AutoMirrored.Filled.ArrowBack, tint = AppColors.textPrimary)
                    }
                },
                title = { Text(maxLines = 1, overflow = TextOverflow.Ellipsis, style = AppTextStyles.screenTitle, text = "Recent") },
            )
        },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            when {
                state.isLoading -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(modifier = Modifier.size(AppSpacing.progressIndicator), color = AppColors.primary, strokeWidth = AppSpacing.progressStroke)
                }
                entries.isEmpty() -> EmptyState(kind = kind)
                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = AppSpacing.lg, end = AppSpacing.screenPadding, start = AppSpacing.screenPadding, top = AppSpacing.lg),
                    verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
                ) {
                    items(items = entries, key = { it.id }) { saved ->
                        RecentTile(onClick = { selected = saved }, saved = saved)
                    }
                }
            }
        }
    }

    val opened = selected

    if (opened != null) {
        RecentActionsSheet(
            onDelete = {
                selected = null
                onDelete(opened)
                scope.launch { snackbarState.showBrief("That ${kind.noun} was removed from this list. The PDF you saved to Downloads is not affected.") }
            },
            onDismiss = { selected = null },
            onEdit = {
                selected = null
                onEdit(opened)
            },
            onSaveCopy = {
                selected = null
                scope.launch {
                    val location = runCatching { withContext(Dispatchers.IO) { exporter.saveToDownloads(exporter.render(opened.document), opened.document) } }.getOrNull()

                    if (location == null) {
                        snackbarState.showError("That copy could not be saved to Downloads. Check the phone storage and try again.")
                        return@launch
                    }
                    snackbarState.showSuccess(locationMessage(location))
                }
            },
            onShare = {
                selected = null
                scope.launch {
                    val intent = runCatching { withContext(Dispatchers.IO) { exporter.shareIntent(exporter.render(opened.document), opened.document) } }.getOrNull()

                    if (intent == null) {
                        snackbarState.showError("That ${kind.noun} could not be prepared for sharing. Check the phone storage and try again.")
                        return@launch
                    }
                    context.startActivity(Intent.createChooser(intent, "Share ${kind.noun}"))
                }
            },
            saved = opened,
        )
    }
}

@Composable
private fun EmptyState(kind: DocumentKind, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().padding(AppSpacing.screenPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier.size(AppSpacing.emptyStateIcon).clip(CircleShape).background(AppColors.surfaceField),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                modifier = Modifier.size(AppSpacing.iconEmptyState),
                contentDescription = null,
                imageVector = Icons.Outlined.ReceiptLong,
                tint = AppColors.textTertiary,
            )
        }
        Spacer(modifier = Modifier.height(AppSpacing.lg))
        Text(maxLines = 1, style = AppTextStyles.listPrimary, text = "No ${kind.noun}s yet")
        Spacer(modifier = Modifier.height(AppSpacing.sm))
        Text(
            style = AppTextStyles.listSecondary,
            text = "Every ${kind.noun} you save stays here until you delete it, ready to edit or save again.",
            textAlign = TextAlign.Center,
        )
    }
}

private fun locationMessage(location: SaveLocation): String = if (location.isShared) {
    "Copy saved to Downloads. Open it from your Files app under Downloads."
} else {
    "Copy saved to the app's own Downloads folder. This version of Android blocks the shared one, so use Share to send it out."
}
