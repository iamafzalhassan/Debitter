package com.example.debitter.ui.recent

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import com.example.debitter.model.SavedNote
import com.example.debitter.pdf.PdfExporter
import com.example.debitter.ui.components.AppSnackbarHost
import com.example.debitter.ui.components.rememberAppSnackbarState
import com.example.debitter.ui.recent.components.NoteActionsSheet
import com.example.debitter.ui.recent.components.RecentNoteTile
import com.example.debitter.ui.theme.AppColors
import com.example.debitter.ui.theme.AppSpacing
import com.example.debitter.ui.theme.AppTextStyles
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val NOTE_LINES: Int = 2

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecentScreen(onBack: () -> Unit, onDelete: (String) -> Unit, onEdit: (SavedNote) -> Unit, onMessageShown: () -> Unit, message: String?, state: RecentState, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val exporter = remember(context) { PdfExporter(context) }
    val scope = rememberCoroutineScope()
    val snackbarState = rememberAppSnackbarState()

    var selected by remember { mutableStateOf<SavedNote?>(null) }

    LaunchedEffect(message) {
        if (message == null) return@LaunchedEffect
        snackbarState.showSuccess(message)
        onMessageShown()
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
            RetentionNote()
            when {
                state.isLoading -> Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(color = AppColors.primary, modifier = Modifier.size(AppSpacing.progressIndicator), strokeWidth = AppSpacing.progressStroke)
                }
                state.notes.isEmpty() -> EmptyState()
                else -> LazyColumn(
                    contentPadding = PaddingValues(bottom = AppSpacing.xl, start = AppSpacing.screenPadding, end = AppSpacing.screenPadding, top = AppSpacing.lg),
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
                ) {
                    items(items = state.notes, key = { it.id }) { saved ->
                        RecentNoteTile(onClick = { selected = saved }, saved = saved)
                    }
                }
            }
        }
    }

    val opened = selected

    if (opened != null) {
        NoteActionsSheet(
            onDelete = {
                selected = null
                onDelete(opened.id)
                scope.launch { snackbarState.showBrief("That note was removed from this list. The PDF you saved to Downloads is not affected.") }
            },
            onDismiss = { selected = null },
            onEdit = {
                selected = null
                onEdit(opened)
            },
            onSaveCopy = {
                selected = null
                scope.launch {
                    val location = runCatching { withContext(Dispatchers.IO) { exporter.saveToDownloads(exporter.render(opened.note), exporter.fileName()) } }.getOrNull()

                    if (location == null) {
                        snackbarState.showError("That copy could not be saved to Downloads. Check the phone storage and try again.")
                        return@launch
                    }
                    snackbarState.showSuccess("Copy saved to Downloads. Open it from your Files app under Downloads.")
                }
            },
            onShare = {
                selected = null
                scope.launch {
                    val intent = runCatching { withContext(Dispatchers.IO) { exporter.shareIntent(exporter.render(opened.note), exporter.fileName()) } }.getOrNull()

                    if (intent == null) {
                        snackbarState.showError("That note could not be prepared for sharing. Check the phone storage and try again.")
                        return@launch
                    }
                    context.startActivity(Intent.createChooser(intent, "Share debit note"))
                }
            },
            saved = opened,
        )
    }
}

@Composable
private fun RetentionNote(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth().background(AppColors.surfaceSunken)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = AppSpacing.screenPadding, vertical = AppSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                contentDescription = null,
                imageVector = Icons.Outlined.Schedule,
                modifier = Modifier.size(AppSpacing.iconHint),
                tint = AppColors.textSecondary,
            )
            Spacer(modifier = Modifier.size(AppSpacing.sm))
            Text(
                maxLines = NOTE_LINES,
                overflow = TextOverflow.Ellipsis,
                style = AppTextStyles.listSecondary,
                text = "Notes are kept for ${RecentViewModel.RETENTION_DAYS} days, then removed from this list.",
            )
        }
        HorizontalDivider(color = AppColors.divider, thickness = AppSpacing.hairline)
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().padding(AppSpacing.screenPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(AppSpacing.emptyStateIcon).clip(CircleShape).background(AppColors.surfaceField),
        ) {
            Icon(
                contentDescription = null,
                imageVector = Icons.Outlined.ReceiptLong,
                modifier = Modifier.size(AppSpacing.iconEmptyState),
                tint = AppColors.textTertiary,
            )
        }
        Spacer(modifier = Modifier.height(AppSpacing.lg))
        Text(maxLines = 1, style = AppTextStyles.listPrimary, text = "No debit notes yet")
        Spacer(modifier = Modifier.height(AppSpacing.sm))
        Text(
            style = AppTextStyles.listSecondary,
            text = "Every note you save appears here for ${RecentViewModel.RETENTION_DAYS} days, ready to edit or save again.",
            textAlign = TextAlign.Center,
        )
    }
}
