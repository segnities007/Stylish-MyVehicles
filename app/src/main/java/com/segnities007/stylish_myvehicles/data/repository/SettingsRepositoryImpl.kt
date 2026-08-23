package com.segnities007.stylish_myvehicles.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.segnities007.stylish_myvehicles.domain.model.ThemeMode
import com.segnities007.stylish_myvehicles.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * SharedPreferences ("stylish_myvehicles_prefs" / "theme_mode") でテーマ設定を永続化するリポジトリ実装。
 * 現在値を StateFlow で保持するため、購読開始直後に最新値が同期的に届く。
 */
class SettingsRepositoryImpl(context: Context) : SettingsRepository {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    private val _themeMode = MutableStateFlow(readThemeMode())
    override fun getThemeMode(): Flow<ThemeMode> = _themeMode.asStateFlow()

    override suspend fun setThemeMode(mode: ThemeMode) {
        prefs.edit()
            .putString(KEY_THEME, mode.name)
            .apply()
        _themeMode.value = mode
    }

    private fun readThemeMode(): ThemeMode {
        val name = prefs.getString(KEY_THEME, ThemeMode.SYSTEM.name)
        return ThemeMode.entries.find { it.name == name } ?: ThemeMode.SYSTEM
    }

    private companion object {
        const val PREF_NAME = "stylish_myvehicles_prefs"
        const val KEY_THEME = "theme_mode"
    }
}
