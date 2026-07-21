package com.segnities007.stylish_myvehicles.presentation.components.molecules.models

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable

@Immutable
data class StylishConnectedCardItem(
    val title: String,
    val supportingText: String = "",
    val onClick: () -> Unit = {},
    val onLongClick: () -> Unit = {},
    val trailingContent: @Composable () -> Unit = {},
)
