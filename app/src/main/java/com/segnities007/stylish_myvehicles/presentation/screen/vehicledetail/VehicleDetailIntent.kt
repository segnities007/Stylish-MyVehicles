package com.segnities007.stylish_myvehicles.presentation.screen.vehicledetail

import com.segnities007.stylish_myvehicles.domain.model.VehicleCategory
import com.segnities007.stylish_myvehicles.presentation.components.organisms.VehicleField
import java.time.LocalDate

sealed interface VehicleDetailIntent {
    data object NavigateBack : VehicleDetailIntent
    data object EditVehicle : VehicleDetailIntent
    data object OpenCostList : VehicleDetailIntent
    data object ExportFuelCsv : VehicleDetailIntent
    data object ExportMaintenanceCsv : VehicleDetailIntent
    data object ExportCostCsv : VehicleDetailIntent

    // フィールド単位の編集
    data class OpenFieldEditor(val field: VehicleField) : VehicleDetailIntent
    data object CloseFieldEditor : VehicleDetailIntent
    data class FieldTextChanged(val value: String) : VehicleDetailIntent
    data class FieldDateChanged(val value: LocalDate?) : VehicleDetailIntent
    data class FieldCategoryChanged(val value: VehicleCategory) : VehicleDetailIntent
    data object SaveField : VehicleDetailIntent
}
