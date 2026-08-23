package com.segnities007.stylish_myvehicles.presentation.screen.settings

import com.segnities007.stylish_myvehicles.domain.model.ThemeMode

sealed interface SettingsIntent {
    data object OpenThemeSelector : SettingsIntent
    data object CloseThemeSelector : SettingsIntent
    data class ThemeModeSelected(val mode: ThemeMode) : SettingsIntent
}
