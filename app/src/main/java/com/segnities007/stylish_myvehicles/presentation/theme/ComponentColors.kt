package com.segnities007.stylish_myvehicles.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp

/**
 * Change this single value to [ComponentColorMode.SYSTEM] to restore the stronger,
 * system-derived container colors.
 */
private val componentColorMode = ComponentColorMode.NEUTRAL

private enum class ComponentColorMode {
    NEUTRAL,
    SYSTEM,
}

@Immutable
data class ComponentColors(
    val groupedContainer: Color,
)

val MaterialTheme.componentColors: ComponentColors
    @Composable get() {
        val scheme = colorScheme
        val groupedContainer = when (componentColorMode) {
            ComponentColorMode.NEUTRAL -> lerp(
                scheme.surface,
                scheme.onSurface,
                0.06f,
            )
            ComponentColorMode.SYSTEM -> scheme.surfaceContainerHighest
        }
        return ComponentColors(groupedContainer = groupedContainer)
    }
