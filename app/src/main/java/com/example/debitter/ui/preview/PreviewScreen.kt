package com.example.debitter.ui.preview

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import com.example.debitter.model.DebitNote
import com.example.debitter.pdf.PdfExporter
import com.example.debitter.pdf.SaveLocation
import com.example.debitter.ui.components.AppSnackbarHost
import com.example.debitter.ui.components.PrimaryButton
import com.example.debitter.ui.components.SecondaryButton
import com.example.debitter.ui.components.rememberAppSnackbarState
import com.example.debitter.ui.theme.AppColors
import com.example.debitter.ui.theme.AppSpacing
import com.example.debitter.ui.theme.AppTextStyles
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val PREVIEW_FILE: String = "preview.pdf"

private const val PREVIEW_SCALE: Int = 2

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreviewScreen(onBack: () -> Unit, onSaved: (SaveLocation) -> Unit, note: DebitNote, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val exporter = remember(context) { PdfExporter(context) }
    val scope = rememberCoroutineScope()
    val snackbarState = rememberAppSnackbarState()
    val document by produceState<PreviewDocument?>(initialValue = null, exporter, note) {
        value = withContext(Dispatchers.IO) { renderDocument(exporter, note) }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            PreviewActions(
                isEnabled = document != null,
                onSave = {
                    val bytes = document?.bytes ?: return@PreviewActions
                    scope.launch {
                        val location = runCatching { withContext(Dispatchers.IO) { exporter.saveToDownloads(bytes, exporter.fileName()) } }.getOrNull()

                        if (location == null) {
                            snackbarState.showError("The debit note could not be saved to Downloads. Check the phone storage and try again.")
                            return@launch
                        }
                        onSaved(location)
                    }
                },
                onShare = {
                    val bytes = document?.bytes ?: return@PreviewActions
                    scope.launch {
                        val intent = runCatching { withContext(Dispatchers.IO) { exporter.shareIntent(bytes, exporter.fileName()) } }.getOrNull()

                        if (intent == null) {
                            snackbarState.showError("The debit note could not be prepared for sharing. Check the phone storage and try again.")
                            return@launch
                        }
                        context.startActivity(Intent.createChooser(intent, "Share debit note"))
                    }
                },
            )
        },
        containerColor = AppColors.surfaceSunken,
        snackbarHost = { AppSnackbarHost(state = snackbarState) },
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AppColors.surfaceBase, scrolledContainerColor = AppColors.surfaceBase),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(contentDescription = "Back", imageVector = Icons.AutoMirrored.Filled.ArrowBack, tint = AppColors.textPrimary)
                    }
                },
                title = { Text(maxLines = 1, overflow = TextOverflow.Ellipsis, style = AppTextStyles.screenTitle, text = "Preview") },
            )
        },
    ) { padding ->
        val pages = document?.pages

        if (pages == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(modifier = Modifier.size(AppSpacing.progressIndicator), color = AppColors.primary, strokeWidth = AppSpacing.progressStroke)
            }
            return@Scaffold
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(AppSpacing.md),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AppSpacing.xl),
        ) {
            itemsIndexed(items = pages) { index, page ->
                Image(
                    modifier = Modifier.widthIn(max = AppSpacing.previewPageMaxWidth).fillMaxWidth().background(AppColors.surfaceCard),
                    bitmap = page,
                    contentDescription = "Page ${index + 1}",
                    contentScale = ContentScale.FillWidth,
                )
            }
        }
    }
}

@Composable
private fun PreviewActions(isEnabled: Boolean, onSave: () -> Unit, onShare: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth().background(AppColors.surfaceCard)) {
        HorizontalDivider(color = AppColors.divider, thickness = AppSpacing.hairline)
        Column(
            modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = AppSpacing.screenPadding, vertical = AppSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.md),
        ) {
            PrimaryButton(modifier = Modifier.fillMaxWidth(), isEnabled = isEnabled, label = "Save", onClick = onSave)
            SecondaryButton(modifier = Modifier.fillMaxWidth(), isEnabled = isEnabled, label = "Share", onClick = onShare)
        }
    }
}

private class PreviewDocument(val bytes: ByteArray, val pages: List<ImageBitmap>)

private fun renderDocument(exporter: PdfExporter, note: DebitNote): PreviewDocument {
    val bytes = exporter.render(note)
    val file = exporter.cacheFile(bytes, PREVIEW_FILE)

    ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY).use { descriptor ->
        PdfRenderer(descriptor).use { renderer ->
            val pages = (0 until renderer.pageCount).map { index -> renderPage(renderer, index) }

            return PreviewDocument(bytes = bytes, pages = pages)
        }
    }
}

private fun renderPage(renderer: PdfRenderer, index: Int): ImageBitmap {
    renderer.openPage(index).use { page ->
        val bitmap = Bitmap.createBitmap(page.width * PREVIEW_SCALE, page.height * PREVIEW_SCALE, Bitmap.Config.ARGB_8888)

        bitmap.eraseColor(Color.WHITE)
        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        return bitmap.asImageBitmap()
    }
}
