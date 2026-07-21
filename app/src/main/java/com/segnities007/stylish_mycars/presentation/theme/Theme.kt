package com.segnities007.stylish_mycars.presentation.theme

import android.os.Build
import androidx.activity.ComponentActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

private val LightColors = lightColorScheme(
    primary = Ink,
    onPrimary = PureSurface,
    primaryContainer = LightPrimaryContainer,
    onPrimaryContainer = LightOnPrimaryContainer,
    secondary = LightSecondary,
    onSecondary = PureSurface,
    secondaryContainer = LightSecondaryContainer,
    onSecondaryContainer = Ink,
    tertiary = LightTertiary,
    onTertiary = PureSurface,
    tertiaryContainer = LightTertiaryContainer,
    onTertiaryContainer = Ink,
    error = LightError,
    onError = LightOnError,
    errorContainer = LightErrorContainer,
    onErrorContainer = LightOnErrorContainer,
    background = Paper,
    onBackground = Ink,
    surface = PureSurface,
    onSurface = Ink,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = Muted,
    outline = LightOutline,
    outlineVariant = SoftOutline,
    inverseSurface = LightInverseSurface,
    inverseOnSurface = LightInverseOnSurface,
    inversePrimary = LightInversePrimary,
    surfaceContainerLowest = LightSurfaceContainerLowest,
    surfaceContainerLow = LightSurfaceContainerLow,
    surfaceContainer = SoftSurface,
    surfaceContainerHigh = LightSurfaceContainerHigh,
    surfaceContainerHighest = LightSurfaceContainerHighest,
    scrim = LightScrim,
)

private val DarkColors = darkColorScheme(
    primary = DarkInk,
    onPrimary = DarkPaper,
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkOnPrimaryContainer,
    secondary = DarkSecondary,
    onSecondary = DarkPaper,
    secondaryContainer = DarkSecondaryContainer,
    onSecondaryContainer = DarkInk,
    tertiary = DarkTertiary,
    onTertiary = DarkPaper,
    tertiaryContainer = DarkTertiaryContainer,
    onTertiaryContainer = DarkInk,
    error = DarkError,
    onError = DarkOnError,
    errorContainer = DarkErrorContainer,
    onErrorContainer = DarkOnErrorContainer,
    background = DarkPaper,
    onBackground = DarkInk,
    surface = DarkSurface,
    onSurface = DarkInk,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkMuted,
    outline = DarkOutlineColor,
    outlineVariant = DarkOutline,
    inverseSurface = DarkInverseSurface,
    inverseOnSurface = DarkInverseOnSurface,
    inversePrimary = DarkInversePrimary,
    surfaceContainerLowest = DarkSurfaceContainerLowest,
    surfaceContainerLow = DarkSurfaceContainerLow,
    surfaceContainer = DarkSoftSurface,
    surfaceContainerHigh = DarkSurfaceContainerHigh,
    surfaceContainerHighest = DarkSurfaceContainerHighest,
    scrim = DarkScrim,
)

@Composable
fun StylishMyCarsTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val darkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    val colors =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && themeMode == ThemeMode.SYSTEM) {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        else {
            if (darkTheme) DarkColors else LightColors
        }
    SideEffect {
        val window = (context as? ComponentActivity)?.window ?: return@SideEffect
        window.decorView.setBackgroundColor(colors.background.toArgb())
    }
    MaterialTheme(colorScheme = colors, typography = Typography, content = content)
}

@Preview(name = "Theme", showBackground = true, widthDp = 393)
@Composable
private fun StylishMyCarsThemePreview() {
    StylishMyCarsTheme {
        Surface(Modifier.padding(20.dp)) {
            Text("StylishMyCarsTheme", style = MaterialTheme.typography.headlineMedium)
        }
    }
}
