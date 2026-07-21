package com.segnities007.stylish_mycars.presentation.screen.vehicledetail

import com.segnities007.stylish_mycars.domain.model.FuelRecord
import com.segnities007.stylish_mycars.domain.model.MaintenanceRecord
import com.segnities007.stylish_mycars.domain.model.MaintenanceSchedule
import com.segnities007.stylish_mycars.domain.model.Vehicle
import java.time.LocalDate

data class VehicleDetailUiState(
    val vehicle: Vehicle? = null,
    val recentFuelRecords: List<FuelRecord> = emptyList(),
    val recentMaintenanceRecords: List<MaintenanceRecord> = emptyList(),
    val schedules: List<MaintenanceSchedule> = emptyList(),
    val monthlyCost: Int = 0,
    val totalCost: Int = 0,
    val averageFuelEconomy: Double? = null,
    val totalDistance: Int = 0,
    val isLoading: Boolean = true,
    // メンテナンス目安編集ダイアログ
    val isScheduleDialogOpen: Boolean = false,
    val editingScheduleId: Long? = null,
    val scheduleInputKm: String = "",
    val scheduleInputMonths: String = "",
    val scheduleInputLastDoneDate: LocalDate? = null,
    val scheduleInputLastDoneOdo: String = "",
)
