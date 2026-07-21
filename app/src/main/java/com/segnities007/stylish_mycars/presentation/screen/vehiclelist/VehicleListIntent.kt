package com.segnities007.stylish_mycars.presentation.screen.vehiclelist

sealed interface VehicleListIntent {
    data object AddVehicle : VehicleListIntent
    data class SelectVehicle(val vehicleId: Long) : VehicleListIntent
    data class SearchQueryChanged(val query: String) : VehicleListIntent
}
