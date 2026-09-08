package com.example.debitter.ui.recent.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import com.example.debitter.model.SavedNote
import com.example.debitter.ui.components.DottedDivider
import com.example.debitter.ui.theme.AppColors
import com.example.debitter.ui.theme.AppSpacing
import com.example.debitter.ui.theme.AppTextStyles
import com.example.debitter.util.MoneyFormat
import com.example.debitter.util.RecentDateFormat
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteActionsSheet(onDelete: () -> Unit, onDismiss: () -> Unit, onEdit: () -> Unit, onSaveCopy: () -> Unit, onShare: () -> Unit, saved: SavedNote) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    val hideThen: (() -> Unit) -> Unit = { action -> scope.launch { sheetState.hide() }.invokeOnCompletion { action() } }

    ModalBottomSheet(containerColor = AppColors.surfaceCard, onDismissRequest = onDismiss, sheetState = sheetState) {
        Column(modifier = Modifier.fillMaxWidth().navigationBarsPadding()) {
            Column(modifier = Modifier.fillMaxWidth().padding(AppSpacing.lg)) {
                Text(
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = AppTextStyles.sectionHeading,
                    text = saved.billTo.ifBlank { saved.note.labels.title },
                )
                Spacer(modifier = Modifier.height(AppSpacing.xs))
                Text(
                    maxLines = 1,
                    style = AppTextStyles.listMeta,
                    text = "${RecentDateFormat.format(saved.createdAt)}  ·  ${MoneyFormat.format(saved.total)}",
                )
            }
            DottedDivider(modifier = Modifier.padding(horizontal = AppSpacing.screenPadding))
            SheetAction(icon = Icons.Outlined.Edit, label = "Edit", onClick = { hideThen(onEdit) })
            SheetAction(icon = Icons.Outlined.Folder, label = "Save a Copy", onClick = { hideThen(onSaveCopy) })
            SheetAction(icon = Icons.Outlined.Share, label = "Share", onClick = { hideThen(onShare) })
            SheetAction(icon = Icons.Outlined.Delete, label = "Delete", onClick = { hideThen(onDelete) })
            Spacer(modifier = Modifier.height(AppSpacing.md))
        }
    }
}

@Composable
private fun SheetAction(label: String, icon: ImageVector, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = AppSpacing.screenPadding, vertical = AppSpacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(modifier = Modifier.size(AppSpacing.iconSheet), contentDescription = null, imageVector = icon, tint = AppColors.primary)
        Spacer(modifier = Modifier.width(AppSpacing.lg))
        Text(maxLines = 1, overflow = TextOverflow.Ellipsis, style = AppTextStyles.body, text = label)
    }
}
