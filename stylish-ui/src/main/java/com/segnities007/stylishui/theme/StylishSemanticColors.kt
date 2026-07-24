package com.segnities007.stylishui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance

@Immutable
data class StylishComponentColors(val groupedContainer: Color)

val MaterialTheme.stylishComponentColors: StylishComponentColors
    @Composable get() {
        val scheme = colorScheme
        return StylishComponentColors(
            groupedContainer = lerp(
                scheme.surface,
                scheme.onSurface,
                if (scheme.background.luminance() > 0.5f) 0.012f else 0.06f,
            ),
        )
    }

@Immutable
data class StylishChartColors(val categorical: List<Color>)

fun ColorScheme.toStylishChartColors() = StylishChartColors(
    categorical = listOf(primary, tertiary, secondary, error, onSurfaceVariant, outline),
)

val MaterialTheme.stylishChartColors: StylishChartColors
    @Composable get() = colorScheme.toStylishChartColors()
