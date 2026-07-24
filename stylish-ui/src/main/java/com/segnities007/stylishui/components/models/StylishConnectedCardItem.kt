package com.segnities007.stylishui.components.models

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable

@Immutable
data class StylishConnectedCardItem(
    val title: String,
    val supportingText: String = "",
    val onClick: (() -> Unit)? = null,
    val onLongClick: (() -> Unit)? = null,
    val trailingContent: @Composable () -> Unit = {},
)
