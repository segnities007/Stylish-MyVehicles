package com.segnities007.stylishui.components.models

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ButtonColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable

@Immutable
data class StylishConnectedButtonItem(
    val onClick: (() -> Unit)? = null,
    val enabled: Boolean = true,
    val colors: ButtonColors? = null,
    val leadingContent: (@Composable RowScope.() -> Unit)? = null,
    val trailingContent: (@Composable RowScope.() -> Unit)? = null,
    val content: @Composable RowScope.() -> Unit,
)
