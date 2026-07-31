package com.segnities007.stylish_myvehicles.presentation.screen.maintenance

sealed interface MaintenanceRecordEffect {
    data object NavigateBack : MaintenanceRecordEffect
}
