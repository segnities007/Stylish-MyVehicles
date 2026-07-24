package com.segnities007.stylish_myvehicles.presentation.screen.records

import androidx.compose.runtime.Immutable
import com.segnities007.stylish_myvehicles.domain.model.CostRecord
import com.segnities007.stylish_myvehicles.domain.model.FuelRecord
import com.segnities007.stylish_myvehicles.domain.model.MaintenanceRecord
import com.segnities007.stylish_myvehicles.domain.model.Vehicle

@Immutable
data class RecordsUiState(
    val vehicleId: Long = 0,
    val topic: RecordTopic = RecordTopic.COST,
    val periodMode: PeriodMode = PeriodMode.MONTHLY,
    val vehicle: Vehicle? = null,
    val periods: List<Period> = emptyList(),
    val fuelRecords: List<FuelRecord> = emptyList(),
    val maintenanceRecords: List<MaintenanceRecord> = emptyList(),
    val costRecords: List<CostRecord> = emptyList(),
    val isLoading: Boolean = true,
    val currentPage: Int = 0,
) {
    val currentPeriod: Period?
        get() = periods.getOrNull(currentPage)
}
