package com.segnities007.stylish_myvehicles.presentation.screen.records

import android.content.Context
import android.content.SharedPreferences

/**
 * 記録画面のページャー粒度（月/年/週）を永続化する。
 */
object PeriodPreference {
    private const val PREF_NAME = "stylish_myvehicles_prefs"
    private const val KEY_PERIOD_MODE = "records_period_mode"

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun getMode(context: Context): PeriodMode {
        val name = prefs(context).getString(KEY_PERIOD_MODE, PeriodMode.MONTHLY.name)
        return PeriodMode.entries.find { it.name == name } ?: PeriodMode.MONTHLY
    }

    fun setMode(context: Context, mode: PeriodMode) {
        prefs(context).edit()
            .putString(KEY_PERIOD_MODE, mode.name)
            .apply()
    }
}
