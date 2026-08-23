package com.segnities007.stylish_myvehicles.presentation.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.segnities007.stylish_myvehicles.domain.repository.SettingsRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private val _effects = Channel<SettingsEffect>(Channel.BUFFERED)
    val effects: Flow<SettingsEffect> = _effects.receiveAsFlow()

    init {
        viewModelScope.launch {
            settingsRepository.getThemeMode().collect { mode ->
                _uiState.update { it.copy(themeMode = mode) }
            }
        }
    }

    fun accept(intent: SettingsIntent) {
        when (intent) {
            is SettingsIntent.OpenThemeSelector ->
                _uiState.update { it.copy(isThemeSheetVisible = true) }

            is SettingsIntent.CloseThemeSelector ->
                _uiState.update { it.copy(isThemeSheetVisible = false) }

            is SettingsIntent.ThemeModeSelected -> {
                // 選択した時点で即座に永続化する（シートの完了/キャンセルは閉じるだけ）
                _uiState.update { it.copy(themeMode = intent.mode) }
                viewModelScope.launch {
                    settingsRepository.setThemeMode(intent.mode)
                }
            }
        }
    }
}
