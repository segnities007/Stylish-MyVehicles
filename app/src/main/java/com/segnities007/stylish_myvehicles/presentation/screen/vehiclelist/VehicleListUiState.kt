package com.segnities007.stylish_myvehicles.presentation.screen.vehiclelist

import androidx.compose.runtime.Immutable
import com.segnities007.stylish_myvehicles.domain.model.Vehicle

@Immutable
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
