package com.segnities007.stylish_myvehicles.presentation.screen.vehiclepager

import com.segnities007.stylish_myvehicles.domain.model.CostCategory
import com.segnities007.stylish_myvehicles.domain.model.Vehicle

data class VehiclePagerUiState(
    val vehicles: List<Vehicle> = emptyList(),
    val isLoading: Boolean = true,
    val currentPage: Int = 0,
    val dashboardByVehicle: Map<Long, VehicleDashboard> = emptyMap(),
) {
    val currentVehicle: Vehicle?
        get() = vehicles.getOrNull(currentPage)

    fun dashboardFor(vehicleId: Long): VehicleDashboard =
        dashboardByVehicle[vehicleId] ?: VehicleDashboard()
}

data class VehicleDashboard(
    val monthlyCost: Int = 0,
    val yearlyCost: Int = 0,
    val totalCost: Int = 0,
    val averageMonthlyCost: Double? = null,
    val averageFuelEconomy: Double? = null,
    val totalDistance: Int = 0,
    val costPerKm: Double? = null,
    val nextMaintenanceLabel: String? = null,
    val nextMaintenanceDays: Long? = null,
    // グラフ用（label と値のペア。色はComposable側でテーマから解決する）
    val costByCategory: List<Pair<CostCategory, Int>> = emptyList(),
    val monthlyCostTrend: List<Pair<String, Float>> = emptyList(),
    val fuelEconomyTrend: List<Pair<String, Float>> = emptyList(),
)
