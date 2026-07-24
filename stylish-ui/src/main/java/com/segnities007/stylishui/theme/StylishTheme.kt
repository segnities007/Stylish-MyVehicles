package com.segnities007.stylishui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable

/**
 * Pure theme boundary. Theme selection and persistence belong to the host application.
 */
@Composable
fun StylishTheme(
    darkTheme: Boolean,
    colorScheme: ColorScheme = if (darkTheme) {
        StylishDarkColorScheme
    } else {
        StylishLightColorScheme
    },
    typography: Typography = StylishTypography,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        content = content,
    )
}
