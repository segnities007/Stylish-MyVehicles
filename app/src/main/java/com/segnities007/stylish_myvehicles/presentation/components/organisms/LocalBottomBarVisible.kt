package com.segnities007.stylish_myvehicles.presentation.components.organisms

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf

/** FNBの表示状態をスクリーンからナビゲーション階層に伝播するためのCompositionLocal。 */
val LocalBottomBarVisible = compositionLocalOf<MutableState<Boolean>> { mutableStateOf(true) }
