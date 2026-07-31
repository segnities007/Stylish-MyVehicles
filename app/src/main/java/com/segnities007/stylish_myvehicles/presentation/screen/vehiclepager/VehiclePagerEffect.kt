package com.segnities007.stylish_myvehicles.presentation.screen.vehiclepager

sealed interface VehiclePagerEffect {
    data class NavigateToEdit(val vehicleId: Long?) : VehiclePagerEffect
    data class NavigateToFuel(val vehicleId: Long) : VehiclePagerEffect
    data class NavigateToMaintenance(val vehicleId: Long) : VehiclePagerEffect
    data class NavigateToCost(val vehicleId: Long) : VehiclePagerEffect
    data class NavigateToTrip(val vehicleId: Long) : VehiclePagerEffect
    data class NavigateToVehicleDetail(val vehicleId: Long) : VehiclePagerEffect
}
