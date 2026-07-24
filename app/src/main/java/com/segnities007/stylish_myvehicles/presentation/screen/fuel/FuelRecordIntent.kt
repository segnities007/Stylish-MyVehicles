package com.segnities007.stylish_myvehicles.presentation.screen.fuel

import com.segnities007.stylish_myvehicles.domain.model.RecordPeriod
import java.time.LocalDate

sealed interface FuelRecordIntent {
    data class SelectPeriod(val period: RecordPeriod) : FuelRecordIntent
    data object OpenAddDialog : FuelRecordIntent
    data class EditRecord(val recordId: Long) : FuelRecordIntent
    data object CloseDialog : FuelRecordIntent
    data class DateChanged(val value: LocalDate) : FuelRecordIntent
    data class OdometerChanged(val value: String) : FuelRecordIntent
    data class VolumeChanged(val value: String) : FuelRecordIntent
    data class AmountChanged(val value: String) : FuelRecordIntent
    data class FullTankChanged(val value: Boolean) : FuelRecordIntent
    data class ReceiptScanned(
        val volume: String?,
        val amount: String?,
        val odometer: String?,
    ) : FuelRecordIntent

    data class ScanningChanged(val value: Boolean) : FuelRecordIntent
    data object Save : FuelRecordIntent
    data class RequestDelete(val recordId: Long) : FuelRecordIntent
    data object ConfirmDelete : FuelRecordIntent
    data object DismissDelete : FuelRecordIntent
    data object NavigateBack : FuelRecordIntent
}
