package com.segnities007.stylish_myvehicles.presentation.screen.fuel

import androidx.compose.runtime.Immutable
import com.segnities007.stylish_myvehicles.domain.model.FuelRecord
import com.segnities007.stylish_myvehicles.domain.model.RecordPeriod
import java.time.LocalDate

@Immutable
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

    val filteredAverageEconomy: Double?
        get() = filteredRecords.mapNotNull { it.fuelEconomy }
            .takeIf { it.isNotEmpty() }
            ?.average()

    val filteredTotalAmount: Int
        get() = filteredRecords.sumOf { it.amount }

    val filteredTotalVolume: Double
        get() = filteredRecords.sumOf { it.volume }

    val canSave: Boolean
        get() = inputOdometer.isNotBlank()
                && inputVolume.isNotBlank()
                && inputAmount.isNotBlank()
                && inputOdometer.toIntOrNull() != null
                && inputVolume.toDoubleOrNull() != null
                && inputAmount.toIntOrNull() != null

    val odometerError: String?
        get() = when {
            inputOdometer.isBlank() -> "走行距離は必須です"
            inputOdometer.toIntOrNull() == null -> "数値で入力してください"
            else -> null
        }

    val volumeError: String?
        get() = when {
            inputVolume.isBlank() -> "給油量は必須です"
            inputVolume.toDoubleOrNull() == null -> "数値で入力してください"
            else -> null
        }

    val amountError: String?
        get() = when {
            inputAmount.isBlank() -> "金額は必須です"
            inputAmount.toIntOrNull() == null -> "数値で入力してください"
            else -> null
        }

    val isEditing: Boolean
        get() = editingRecordId != null
}
