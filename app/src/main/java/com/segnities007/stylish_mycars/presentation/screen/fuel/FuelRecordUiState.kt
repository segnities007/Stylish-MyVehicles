package com.segnities007.stylish_mycars.presentation.screen.fuel

import com.segnities007.stylish_mycars.domain.model.FuelRecord
import com.segnities007.stylish_mycars.domain.model.RecordPeriod
import java.time.LocalDate

data class FuelRecordUiState(
    val vehicleId: Long = 0,
    val records: List<FuelRecord> = emptyList(),
    val isLoading: Boolean = true,
    val selectedPeriod: RecordPeriod = RecordPeriod.ALL,
    // ダイアログ
    val isDialogOpen: Boolean = false,
    val editingRecordId: Long? = null,
    val inputDate: LocalDate = LocalDate.now(),
    val inputOdometer: String = "",
    val inputVolume: String = "",
    val inputAmount: String = "",
    val inputIsFullTank: Boolean = true,
    val calculatedEconomy: String? = null,
    // レシートスキャン
    val isScanning: Boolean = false,
    // 削除確認
    val deletingRecordId: Long? = null,
) {
    /** 選択期間で絞り込んだ表示用レコード。 */
    val filteredRecords: List<FuelRecord>
        get() = selectedPeriod.filter(records) { it.date }

    val canSave: Boolean
        get() = inputOdometer.isNotBlank()
                && inputVolume.isNotBlank()
                && inputAmount.isNotBlank()
                && inputOdometer.toIntOrNull() != null
                && inputVolume.toDoubleOrNull() != null
                && inputAmount.toIntOrNull() != null

    val isEditing: Boolean
        get() = editingRecordId != null
}
