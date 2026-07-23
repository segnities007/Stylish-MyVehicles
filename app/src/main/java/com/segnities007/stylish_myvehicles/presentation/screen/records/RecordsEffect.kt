package com.segnities007.stylish_myvehicles.presentation.screen.records

sealed interface RecordsEffect {
    data object NavigateBack : RecordsEffect
}
