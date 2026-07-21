package com.segnities007.stylish_mycars.presentation.components.molecules.models

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable

@Immutable
data class StylishConnectedListItem(
    val headline: String,
    val supportingText: String? = null,
    val onClick: () -> Unit,
    val enabled: Boolean = true,
    val leadingContent: (@Composable RowScope.() -> Unit)? = null,
    val trailingContent: (@Composable RowScope.() -> Unit)? = null,
)
