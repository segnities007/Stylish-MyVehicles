package com.segnities007.stylish_myvehicles.presentation.screen.maintenance

import androidx.compose.runtime.Immutable
import com.segnities007.stylish_myvehicles.domain.model.MaintenanceCategory
import com.segnities007.stylish_myvehicles.domain.model.MaintenanceRecord
import com.segnities007.stylish_myvehicles.domain.model.RecordPeriod
import java.time.LocalDate

@Immutable
data class MaintenanceRecordUiState(
    val vehicleId: Long = 0,
    val records: List<MaintenanceRecord> = emptyList(),
    val isLoading: Boolean = true,
    val relevantCategories: List<MaintenanceCategory> = MaintenanceCategory.entries,
    val selectedPeriod: RecordPeriod = RecordPeriod.ALL,
    val scheduleDueItems: List<ScheduleDueItem> = emptyList(),
    // ダイアログ
    val isDialogOpen: Boolean = false,
    val editingRecordId: Long? = null,
    val inputDate: LocalDate = LocalDate.now(),
    val inputOdometer: String = "",
    val inputCategory: MaintenanceCategory = MaintenanceCategory.OTHER,
    val inputTitle: String = "",
    val inputCost: String = "",
    val inputShopName: String = "",
    // 削除確認
    val deletingRecordId: Long? = null,
) {
    /** 選択期間で絞り込んだ表示用レコード。 */
    val filteredRecords: List<MaintenanceRecord>
        get() = selectedPeriod.filter(records) { it.date }

    val filteredTotalCost: Int
        get() = filteredRecords.sumOf { it.cost }

    val filteredCount: Int
        get() = filteredRecords.size

    val canSave: Boolean
        get() = inputTitle.isNotBlank()

    val titleError: String?
        get() = if (inputTitle.isBlank()) "整備内容は必須です" else null

    val isEditing: Boolean
        get() = editingRecordId != null
}

@Immutable
data class ScheduleDueItem(
    val categoryLabel: String,
    val dueDate: LocalDate,
    val daysRemaining: Long,
) {
    val isOverdue: Boolean get() = daysRemaining < 0
    val isDueSoon: Boolean get() = daysRemaining in 0..30
}
