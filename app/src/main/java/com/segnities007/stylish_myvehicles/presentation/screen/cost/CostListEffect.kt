package com.segnities007.stylish_myvehicles.presentation.screen.cost

sealed interface CostListEffect {
    data object NavigateBack : CostListEffect
}
