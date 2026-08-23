package com.segnities007.stylish_myvehicles.presentation.screen.settings

import androidx.compose.runtime.Immutable
import com.segnities007.stylish_myvehicles.domain.model.ThemeMode

@Immutable
data class SettingsUiState(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val isThemeSheetVisible: Boolean = false,
)
