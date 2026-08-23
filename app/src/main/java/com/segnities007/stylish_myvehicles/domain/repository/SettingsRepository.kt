package com.segnities007.stylish_myvehicles.domain.repository

import com.segnities007.stylish_myvehicles.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getThemeMode(): Flow<ThemeMode>
    suspend fun setThemeMode(mode: ThemeMode)
}
