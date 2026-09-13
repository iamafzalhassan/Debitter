package com.example.debitter.ui.settings.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import com.example.debitter.ui.components.PresetTile
import com.example.debitter.ui.components.SecondaryButton
import com.example.debitter.ui.components.SectionHeader
import com.example.debitter.ui.theme.AppSpacing
import com.example.debitter.ui.theme.AppTextStyles

fun <T> LazyListScope.directorySection(
    addLabel: String,
    emptyText: String,
    heading: String,
    intro: String,
    entries: List<T>,
    onAdd: () -> Unit,
    detail: (T) -> String,
    id: (T) -> String,
    name: (T) -> String,
    onOpen: (T) -> Unit,
) {
    item(key = "heading-$heading") {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = AppSpacing.screenPadding)) {
            SectionHeader(label = heading)
            Text(style = AppTextStyles.listSecondary, text = intro)
            Spacer(modifier = Modifier.height(AppSpacing.md))
        }
    }
    if (entries.isEmpty()) {
        item(key = "empty-$heading") {
            Text(
                modifier = Modifier.fillMaxWidth().padding(bottom = AppSpacing.sm, end = AppSpacing.screenPadding, start = AppSpacing.screenPadding),
                style = AppTextStyles.listSecondary,
                text = emptyText,
            )
        }
    }
    items(items = entries, key = id) { entry ->
        PresetTile(
            modifier = Modifier.padding(bottom = AppSpacing.sm, end = AppSpacing.screenPadding, start = AppSpacing.screenPadding),
            detail = detail(entry),
            name = name(entry),
            onClick = { onOpen(entry) },
        )
    }
    item(key = "action-$heading") {
        SecondaryButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppSpacing.screenPadding)
                .padding(bottom = AppSpacing.xl, top = AppSpacing.sm),
            label = addLabel,
            onClick = onAdd,
        )
    }
}
