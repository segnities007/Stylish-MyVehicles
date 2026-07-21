package com.segnities007.stylish_mycars.presentation.screen.vehiclepager

sealed interface VehiclePagerIntent {
    data object AddVehicle : VehiclePagerIntent
    data object OpenSettings : VehiclePagerIntent
    data class EditVehicle(val vehicleId: Long) : VehiclePagerIntent
    data class OpenFuel(val vehicleId: Long) : VehiclePagerIntent
    data class OpenMaintenance(val vehicleId: Long) : VehiclePagerIntent
    data class OpenCost(val vehicleId: Long) : VehiclePagerIntent
    data class PageChanged(val page: Int) : VehiclePagerIntent
}
