package com.segnities007.stylish_mycars.presentation.theme

import android.content.Context
import android.content.SharedPreferences

enum class ThemeMode(val label: String) {
    SYSTEM("システム設定に従う"),
    LIGHT("ライト"),
    DARK("ダーク"),
}

object ThemePreference {
    private const val PREF_NAME = "stylish_mycars_prefs"
    private const val KEY_THEME = "theme_mode"

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun getThemeMode(context: Context): ThemeMode {
        val name = prefs(context).getString(KEY_THEME, ThemeMode.SYSTEM.name)
        return ThemeMode.entries.find { it.name == name } ?: ThemeMode.SYSTEM
    }

    fun setThemeMode(context: Context, mode: ThemeMode) {
        prefs(context).edit().putString(KEY_THEME, mode.name).apply()
    }
}
