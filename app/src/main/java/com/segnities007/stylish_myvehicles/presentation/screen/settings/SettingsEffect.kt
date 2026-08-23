package com.segnities007.stylish_myvehicles.presentation.screen.settings

/**
 * 設定画面の one-shot イベント。
 * 現状はテーマ選択がすべて UiState 経由で完結するため発行される Effect はないが、
 * MVI 4 要素構成（Intent / UiState / Effect / ViewModel）に従い定義しておく。
 */
sealed interface SettingsEffect
