package com.segnities007.stylish_myvehicles.presentation.theme

import android.os.Build
import androidx.activity.ComponentActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.segnities007.stylishui.theme.StylishDarkColorScheme
import com.segnities007.stylishui.theme.StylishLightColorScheme
import com.segnities007.stylishui.theme.StylishTheme

@Composable
fun StylishMyVehiclesTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val darkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    val baseColors =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        else {
            if (darkTheme) StylishDarkColorScheme else StylishLightColorScheme
        }
    val colors = if (darkTheme) baseColors else baseColors.withLightSurfaceHierarchy()
    SideEffect {
        val window = (context as? ComponentActivity)?.window ?: return@SideEffect
        window.decorView.setBackgroundColor(colors.background.toArgb())
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = !darkTheme
            isAppearanceLightNavigationBars = !darkTheme
        }
    }
    StylishTheme(
        darkTheme = darkTheme,
        colorScheme = colors,
        content = content,
    )
}

/**
 * Dynamic Colorでも「背景 < コンテナ < 強調操作」の明度順を保つ。
 * 色相は端末のシステムカラーを維持し、surfaceとの混色量だけを調整する。
 */
private fun ColorScheme.withLightSurfaceHierarchy(): ColorScheme = copy(
    background = lerp(surface, onSurface, 0.055f),
    surfaceContainerLowest = surface,
    surfaceContainerLow = lerp(surface, onSurface, 0.008f),
    surfaceContainer = lerp(surface, onSurface, 0.018f),
    surfaceContainerHigh = lerp(surface, onSurface, 0.012f),
    surfaceContainerHighest = lerp(surface, onSurface, 0.035f),
)

@Preview(name = "Theme", showBackground = true, widthDp = 393)
@Composable
private fun StylishMyVehiclesThemePreview() {
    StylishMyVehiclesTheme {
        Surface(Modifier.padding(20.dp)) {
            Text("StylishMyVehiclesTheme", style = MaterialTheme.typography.headlineMedium)
        }
    }
}
