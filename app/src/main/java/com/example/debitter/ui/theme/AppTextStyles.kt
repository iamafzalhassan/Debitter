package com.example.debitter.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.debitter.R

object AppTextStyles {
    private val interFamily: FontFamily = FontFamily(
        Font(R.font.inter_regular, FontWeight.Normal),
        Font(R.font.inter_medium, FontWeight.Medium),
        Font(R.font.inter_semibold, FontWeight.SemiBold),
        Font(R.font.inter_bold, FontWeight.Bold),
    )

    val amount: TextStyle = style(size = 15, weight = FontWeight.SemiBold, height = 1.3f, color = AppColors.textPrimary, tabular = true)
    val body: TextStyle = style(size = 15, weight = FontWeight.Normal, height = 1.3f, color = AppColors.textPrimary)
    val button: TextStyle = style(size = 16, weight = FontWeight.SemiBold, height = 1.2f, color = AppColors.primaryOn, tracking = 0.2f)
    val errorHint: TextStyle = style(size = 12, weight = FontWeight.Medium, height = 1.3f, color = AppColors.danger)
    val fieldLabel: TextStyle = style(size = 11, weight = FontWeight.Medium, height = 1.3f, color = AppColors.textSecondary)
    val fieldValue: TextStyle = style(size = 15, weight = FontWeight.SemiBold, height = 1.35f, color = AppColors.textPrimary, tabular = true)
    val label: TextStyle = style(size = 14, weight = FontWeight.Normal, height = 1.2f, color = AppColors.textSecondary)
    val listMeta: TextStyle = style(size = 13, weight = FontWeight.Normal, height = 1.3f, color = AppColors.textSecondary, tabular = true)
    val listPrimary: TextStyle = style(size = 15, weight = FontWeight.SemiBold, height = 1.3f, color = AppColors.textPrimary)
    val listSecondary: TextStyle = style(size = 13, weight = FontWeight.Normal, height = 1.3f, color = AppColors.textSecondary)
    val overline: TextStyle = style(size = 11, weight = FontWeight.SemiBold, height = 1.2f, color = AppColors.textSecondary, tracking = 0.8f)
    val screenTitle: TextStyle = style(size = 20, weight = FontWeight.Bold, height = 1.2f, color = AppColors.textPrimary)
    val sectionHeading: TextStyle = style(size = 17, weight = FontWeight.SemiBold, height = 1.2f, color = AppColors.textPrimary)
    val snack: TextStyle = style(size = 14, weight = FontWeight.Medium, height = 1.3f, color = AppColors.primaryOn)
    val totalsValue: TextStyle = style(size = 17, weight = FontWeight.Medium, height = 1.3f, color = AppColors.textPrimary, tabular = true)
    val totalsValueBold: TextStyle = style(size = 17, weight = FontWeight.Bold, height = 1.3f, color = AppColors.textPrimary, tabular = true)

    private fun style(size: Int, weight: FontWeight, height: Float, color: Color, tabular: Boolean = false, tracking: Float = 0f): TextStyle = TextStyle(
        color = color,
        fontFamily = interFamily,
        fontFeatureSettings = if (tabular) "tnum" else null,
        fontSize = size.sp,
        fontWeight = weight,
        letterSpacing = tracking.sp,
        lineHeight = (size * height).sp,
    )
}
