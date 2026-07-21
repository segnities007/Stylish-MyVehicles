package com.segnities007.stylish_mycars.presentation.theme

import android.content.Context

object OnboardingPreference {
    private const val PREFS_NAME = "stylish_mycars_prefs"
    private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"

    fun isCompleted(context: Context): Boolean =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_ONBOARDING_COMPLETED, false)

    fun setCompleted(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_ONBOARDING_COMPLETED, true)
            .apply()
    }
}
