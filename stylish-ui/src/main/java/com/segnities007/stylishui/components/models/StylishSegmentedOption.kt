package com.segnities007.stylishui.components.models

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable

@Immutable
data class StylishSegmentedOption<T>(
    val value: T,
    val label: String,
    val leadingContent: (@Composable RowScope.() -> Unit)? = null,
)
