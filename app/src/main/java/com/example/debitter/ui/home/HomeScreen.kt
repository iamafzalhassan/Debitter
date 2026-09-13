package com.example.debitter.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.RequestQuote
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import com.example.debitter.model.DocumentKind
import com.example.debitter.ui.theme.AppColors
import com.example.debitter.ui.theme.AppSpacing
import com.example.debitter.ui.theme.AppTextStyles

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onSettings: () -> Unit, onOpen: (DocumentKind) -> Unit, modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = AppColors.surfaceBase,
        topBar = {
            TopAppBar(
                actions = {
                    IconButton(onClick = onSettings) {
                        Icon(contentDescription = "Settings", imageVector = Icons.Outlined.Settings, tint = AppColors.textSecondary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AppColors.surfaceBase, scrolledContainerColor = AppColors.surfaceBase),
                title = { Text(maxLines = 1, overflow = TextOverflow.Ellipsis, style = AppTextStyles.screenTitle, text = "Debitter") },
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(bottom = AppSpacing.xl, end = AppSpacing.screenPadding, start = AppSpacing.screenPadding, top = AppSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
        ) {
            items(items = DocumentKind.entries, key = { it.name }) { kind ->
                DocumentTile(kind = kind, onClick = { onOpen(kind) })
            }
        }
    }
}

@Composable
private fun DocumentTile(onClick: () -> Unit, kind: DocumentKind, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(AppSpacing.radiusCard)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(AppColors.surfaceCard)
            .border(border = BorderStroke(AppSpacing.hairline, AppColors.divider), shape = shape)
            .clickable(onClick = onClick)
            .padding(horizontal = AppSpacing.lg, vertical = AppSpacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(modifier = Modifier.size(AppSpacing.iconSheet), contentDescription = null, imageVector = kind.icon, tint = AppColors.primary)
        Spacer(modifier = Modifier.width(AppSpacing.lg))
        Column(modifier = Modifier.weight(1f)) {
            Text(maxLines = 1, overflow = TextOverflow.Ellipsis, style = AppTextStyles.listPrimary, text = kind.title)
            Spacer(modifier = Modifier.height(AppSpacing.xxs))
            Text(maxLines = 1, overflow = TextOverflow.Ellipsis, style = AppTextStyles.listSecondary, text = kind.summary)
        }
        Icon(
            modifier = Modifier.size(AppSpacing.iconPlaceholder),
            contentDescription = null,
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            tint = AppColors.textSecondary,
        )
    }
}

private val DocumentKind.icon: ImageVector get() = when (this) {
    DocumentKind.DEBIT_NOTE -> Icons.Outlined.RequestQuote
    DocumentKind.REFUND_LETTER -> Icons.Outlined.Description
}
