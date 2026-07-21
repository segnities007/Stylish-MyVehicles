package com.segnities007.stylish_myvehicles.presentation.screen.records

import com.segnities007.stylish_myvehicles.domain.model.CostCategory
import com.segnities007.stylish_myvehicles.domain.model.CostRecord
import com.segnities007.stylish_myvehicles.domain.model.FuelRecord
import com.segnities007.stylish_myvehicles.domain.model.MaintenanceRecord
import com.segnities007.stylish_myvehicles.domain.model.Vehicle
import java.time.YearMonth

data class RecordsUiState(
    val vehicleId: Long = 0,
    val vehicle: Vehicle? = null,
    val months: List<YearMonth> = emptyList(),
    val fuelByMonth: Map<YearMonth, List<FuelRecord>> = emptyMap(),
    val maintenanceByMonth: Map<YearMonth, List<MaintenanceRecord>> = emptyMap(),
    val costByMonth: Map<YearMonth, List<CostRecord>> = emptyMap(),
    val isLoading: Boolean = true,
    val currentPage: Int = 0,
    val monthlyCost: Int = 0,
    val yearlyCost: Int = 0,
    val totalCost: Int = 0,
    val averageMonthlyCost: Double? = null,
    val averageFuelEconomy: Double? = null,
    val totalDistance: Int = 0,
    val costByCategory: List<Pair<CostCategory, Int>> = emptyList(),
    val monthlyCostTrend: List<Pair<String, Float>> = emptyList(),
) {
    val currentMonth: YearMonth?
        get() = months.getOrNull(currentPage)
}
