package com.segnities007.stylishui.tokens

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** StylishUI全体で共有する空間・輪郭・奥行きの基礎トークン。 */
@Immutable
object StylishDimensions {
    val connectedSpacing: Dp = 3.dp
    val outlineWidth: Dp = 0.4.dp

    val interactiveElevation: Dp = 1.dp
    val floatingElevation: Dp = 2.dp

    val connectedCornerRadius: Dp = 12.dp
    val joinedCornerRadius: Dp = 2.dp
    val floatingCornerRadius: Dp = 28.dp
}
