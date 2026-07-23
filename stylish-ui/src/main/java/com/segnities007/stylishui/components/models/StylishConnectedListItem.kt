package com.segnities007.stylishui.components.models

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable

@Immutable
data class StylishConnectedListItem(
    val headline: String,
    val supportingText: String? = null,
    val onClick: (() -> Unit)? = null,
    val onLongClick: (() -> Unit)? = null,
    val enabled: Boolean = true,
    val leadingContent: (@Composable RowScope.() -> Unit)? = null,
    val trailingContent: (@Composable RowScope.() -> Unit)? = null,
    val supportingLines: List<String> = emptyList(),
)
