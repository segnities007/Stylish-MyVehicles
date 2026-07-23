package com.segnities007.stylish_myvehicles.presentation.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * Semantic data-visualization colors derived from the active Material color scheme.
 *
 * Keeping this palette next to the app theme means charts also follow system dynamic colors
 * instead of maintaining an unrelated set of fixed colors.
 */
@Immutable
data class ChartColors(
    val categorical: List<Color>,
)

fun ColorScheme.toChartColors(): ChartColors = ChartColors(
    categorical = listOf(
        primary,
        tertiary,
        secondary,
        error,
        onSurfaceVariant,
        outline,
    ),
)

val MaterialTheme.chartColors: ChartColors
    @Composable get() = colorScheme.toChartColors()
