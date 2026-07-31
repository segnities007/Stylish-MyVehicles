package com.segnities007.stylish_myvehicles.presentation.screen.vehicleedit

sealed interface VehicleEditEffect {
    data object NavigateBack : VehicleEditEffect
}
