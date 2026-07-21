package com.segnities007.stylish_mycars.presentation.screen.maintenance

import com.segnities007.stylish_mycars.domain.model.MaintenanceCategory
import com.segnities007.stylish_mycars.domain.model.MaintenanceRecord
import com.segnities007.stylish_mycars.domain.model.RecordPeriod
import java.time.LocalDate

data class MaintenanceRecordUiState(
    val vehicleId: Long = 0,
    val records: List<MaintenanceRecord> = emptyList(),
    val isLoading: Boolean = true,
    val relevantCategories: List<MaintenanceCategory> = MaintenanceCategory.entries,
    val selectedPeriod: RecordPeriod = RecordPeriod.ALL,
    // ダイアログ
    val isDialogOpen: Boolean = false,
    val editingRecordId: Long? = null,
    val inputDate: LocalDate = LocalDate.now(),
    val inputOdometer: String = "",
    val inputCategory: MaintenanceCategory = MaintenanceCategory.OTHER,
    val inputTitle: String = "",
    val inputCost: String = "",
    val inputShopName: String = "",
    val inputMemo: String = "",
    val inputPhotoUri: String? = null,
    // 削除確認
    val deletingRecordId: Long? = null,
) {
    /** 選択期間で絞り込んだ表示用レコード。 */
    val filteredRecords: List<MaintenanceRecord>
        get() = selectedPeriod.filter(records) { it.date }

    val canSave: Boolean
        get() = inputTitle.isNotBlank()

    val isEditing: Boolean
        get() = editingRecordId != null
}
