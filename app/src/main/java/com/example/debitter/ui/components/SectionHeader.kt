package com.example.debitter.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.example.debitter.ui.theme.AppSpacing
import com.example.debitter.ui.theme.AppTextStyles
import java.util.Locale

@Composable
fun SectionHeader(label: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(maxLines = 1, overflow = TextOverflow.Ellipsis, style = AppTextStyles.overline, text = label.uppercase(Locale.ROOT))
        Spacer(modifier = Modifier.height(AppSpacing.sm))
        DottedDivider()
        Spacer(modifier = Modifier.height(AppSpacing.lg))
    }
}
