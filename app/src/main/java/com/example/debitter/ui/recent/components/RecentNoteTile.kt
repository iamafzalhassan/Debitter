package com.example.debitter.ui.recent.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import com.example.debitter.model.SavedNote
import com.example.debitter.ui.theme.AppColors
import com.example.debitter.ui.theme.AppSpacing
import com.example.debitter.ui.theme.AppTextStyles
import com.example.debitter.util.MoneyFormat
import com.example.debitter.util.RecentDateFormat

@Composable
fun RecentNoteTile(onClick: () -> Unit, saved: SavedNote, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(AppSpacing.radiusCard)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(AppColors.surfaceCard)
            .border(border = BorderStroke(AppSpacing.hairline, AppColors.divider), shape = shape)
            .clickable(onClick = onClick)
            .padding(AppSpacing.cardPadding),
    ) {
        Text(
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = AppTextStyles.listPrimary,
            text = saved.billTo.ifBlank { saved.note.labels.title },
        )
        Spacer(modifier = Modifier.height(AppSpacing.xs))
        Text(
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = AppTextStyles.listMeta,
            text = RecentDateFormat.format(saved.createdAt),
        )
        Spacer(modifier = Modifier.height(AppSpacing.sm))
        Text(maxLines = 1, style = AppTextStyles.totalsValueBold, text = MoneyFormat.format(saved.total))
    }
}
