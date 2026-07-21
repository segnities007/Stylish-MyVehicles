package com.segnities007.stylish_mycars.presentation.screen.vehicledetail

import java.time.LocalDate

sealed interface VehicleDetailIntent {
    data object NavigateBack : VehicleDetailIntent
    data object EditVehicle : VehicleDetailIntent
    data object OpenFuelRecords : VehicleDetailIntent
    data object OpenMaintenanceRecords : VehicleDetailIntent
    data object OpenCostList : VehicleDetailIntent
    data object ExportFuelCsv : VehicleDetailIntent
    data object ExportMaintenanceCsv : VehicleDetailIntent
    data object ExportCostCsv : VehicleDetailIntent

    // メンテナンス目安の編集
    data class EditSchedule(val scheduleId: Long) : VehicleDetailIntent
    data object CloseScheduleDialog : VehicleDetailIntent
    data class ScheduleIntervalKmChanged(val value: String) : VehicleDetailIntent
    data class ScheduleIntervalMonthsChanged(val value: String) : VehicleDetailIntent
    data class ScheduleLastDoneDateChanged(val value: LocalDate?) : VehicleDetailIntent
    data class ScheduleLastDoneOdometerChanged(val value: String) : VehicleDetailIntent
    data object SaveSchedule : VehicleDetailIntent
}
