package com.example.debitter.ui.theme

import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

private const val SELECTION_ALPHA: Float = 0.24f

private val DebitterColorScheme: ColorScheme = lightColorScheme(
    background = AppColors.surfaceBase,
    error = AppColors.danger,
    errorContainer = AppColors.danger,
    inverseSurface = AppColors.surfaceInverse,
    onBackground = AppColors.textPrimary,
    onError = AppColors.primaryOn,
    onErrorContainer = AppColors.primaryOn,
    onInverseSurface = AppColors.primaryOn,
    onPrimary = AppColors.primaryOn,
    onSecondary = AppColors.primaryOn,
    onSurface = AppColors.textPrimary,
    onSurfaceVariant = AppColors.textSecondary,
    outline = AppColors.divider,
    outlineVariant = AppColors.divider,
    primary = AppColors.primary,
    primaryContainer = AppColors.primary,
    scrim = AppColors.surfaceInverse,
    secondary = AppColors.primary,
    surface = AppColors.surfaceCard,
    surfaceContainer = AppColors.surfaceCard,
    surfaceContainerHigh = AppColors.surfaceCard,
    surfaceContainerHighest = AppColors.surfaceField,
    surfaceContainerLow = AppColors.surfaceBase,
    surfaceContainerLowest = AppColors.surfaceBase,
    surfaceVariant = AppColors.surfaceField,
    tertiary = AppColors.primary,
)

private val DebitterTypography: Typography = Typography(
    bodyLarge = AppTextStyles.body,
    bodyMedium = AppTextStyles.listSecondary,
    bodySmall = AppTextStyles.errorHint,
    labelLarge = AppTextStyles.button,
    labelMedium = AppTextStyles.label,
    labelSmall = AppTextStyles.fieldLabel,
    titleLarge = AppTextStyles.screenTitle,
    titleMedium = AppTextStyles.sectionHeading,
    titleSmall = AppTextStyles.listPrimary,
)

@Composable
fun DebitterTheme(content: @Composable () -> Unit) {
    val selectionColors = TextSelectionColors(backgroundColor = AppColors.primary.copy(alpha = SELECTION_ALPHA), handleColor = AppColors.primary)

    CompositionLocalProvider(LocalTextSelectionColors provides selectionColors) {
        MaterialTheme(colorScheme = DebitterColorScheme, typography = DebitterTypography, content = content)
    }
}
