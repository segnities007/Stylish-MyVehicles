package com.segnities007.stylish_myvehicles.presentation.screen.maintenance

import com.segnities007.stylish_myvehicles.domain.model.MaintenanceCategory
import com.segnities007.stylish_myvehicles.domain.model.RecordPeriod
import java.time.LocalDate

sealed interface MaintenanceRecordIntent {
    data class SelectPeriod(val period: RecordPeriod) : MaintenanceRecordIntent
    data object OpenAddDialog : MaintenanceRecordIntent
    data class EditRecord(val recordId: Long) : MaintenanceRecordIntent
    data object CloseDialog : MaintenanceRecordIntent
    data class DateChanged(val value: LocalDate) : MaintenanceRecordIntent
    data class OdometerChanged(val value: String) : MaintenanceRecordIntent
    data class CategoryChanged(val value: MaintenanceCategory) : MaintenanceRecordIntent
    data class TitleChanged(val value: String) : MaintenanceRecordIntent
    data class CostChanged(val value: String) : MaintenanceRecordIntent
    data class ShopNameChanged(val value: String) : MaintenanceRecordIntent
    data object Save : MaintenanceRecordIntent
    data class RequestDelete(val recordId: Long) : MaintenanceRecordIntent
    data object ConfirmDelete : MaintenanceRecordIntent
    data object DismissDelete : MaintenanceRecordIntent
    data object NavigateBack : MaintenanceRecordIntent
}
