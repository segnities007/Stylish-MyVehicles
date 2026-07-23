package com.segnities007.stylishui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private fun style(weight: FontWeight, size: Int, lineHeight: Int) = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = weight,
    fontSize = size.sp,
    lineHeight = lineHeight.sp,
)

val StylishTypography = Typography(
    displayLarge = style(FontWeight.Bold, 28, 36),
    displayMedium = style(FontWeight.Bold, 24, 32),
    displaySmall = style(FontWeight.Bold, 22, 28),
    headlineLarge = style(FontWeight.SemiBold, 24, 32),
    headlineMedium = style(FontWeight.SemiBold, 20, 28),
    headlineSmall = style(FontWeight.SemiBold, 18, 24),
    titleLarge = style(FontWeight.SemiBold, 18, 26),
    titleMedium = style(FontWeight.SemiBold, 16, 24),
    titleSmall = style(FontWeight.Medium, 14, 20),
    bodyLarge = style(FontWeight.Normal, 16, 24),
    bodyMedium = style(FontWeight.Normal, 14, 20),
    bodySmall = style(FontWeight.Normal, 12, 16),
    labelLarge = style(FontWeight.Medium, 14, 20),
    labelMedium = style(FontWeight.Medium, 12, 16),
    labelSmall = style(FontWeight.Medium, 11, 16),
)
