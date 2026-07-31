package com.segnities007.stylish_myvehicles.presentation.screen.vehiclelist

sealed interface VehicleListEffect {
    data class NavigateToDetail(val vehicleId: Long) : VehicleListEffect
    data class NavigateToEdit(val vehicleId: Long?) : VehicleListEffect
}
