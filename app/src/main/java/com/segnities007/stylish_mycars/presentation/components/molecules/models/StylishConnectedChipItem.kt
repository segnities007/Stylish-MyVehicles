package com.segnities007.stylish_mycars.presentation.components.molecules.models

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable

@Immutable
data class StylishConnectedChipItem(
    val label: String,
    val onClick: () -> Unit,
    val selected: Boolean = false,
    val enabled: Boolean = true,
    val leadingContent: (@Composable RowScope.() -> Unit)? = null,
    val trailingContent: (@Composable RowScope.() -> Unit)? = null,
)
