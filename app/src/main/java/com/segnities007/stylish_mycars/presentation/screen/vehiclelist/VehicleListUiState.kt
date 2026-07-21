package com.segnities007.stylish_mycars.presentation.screen.vehiclelist

import com.segnities007.stylish_mycars.domain.model.Vehicle

data class VehicleListUiState(
    val vehicles: List<Vehicle> = emptyList(),
    val isLoading: Boolean = true,
    val searchQuery: String = "",
) {
    val filteredVehicles: List<Vehicle>
        get() = if (searchQuery.isBlank()) vehicles
        else vehicles.filter { vehicle ->
            val q = searchQuery.lowercase()
            vehicle.maker.lowercase()
                .contains(q)
                    || vehicle.name.lowercase()
                .contains(q)
                    || vehicle.plateNumber.lowercase()
                .contains(q)
                    || vehicle.grade.lowercase()
                .contains(q)
        }
}
