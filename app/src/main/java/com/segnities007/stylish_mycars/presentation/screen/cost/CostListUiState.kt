package com.segnities007.stylish_mycars.presentation.screen.cost

import com.segnities007.stylish_mycars.domain.model.CostCategory
import com.segnities007.stylish_mycars.domain.model.CostRecord
import java.time.LocalDate

data class CostListUiState(
    val vehicleId: Long = 0,
    val records: List<CostRecord> = emptyList(),
    val isLoading: Boolean = true,
    val selectedCategory: CostCategory? = null,
    val deletingRecordId: Long? = null,
    // 入力ダイアログ
    val isDialogOpen: Boolean = false,
    val editingRecordId: Long? = null,
    val inputCategory: CostCategory = CostCategory.OTHER,
    val inputDate: LocalDate = LocalDate.now(),
    val inputTitle: String = "",
    val inputAmount: String = "",
    val inputMemo: String = "",
) {
    val filteredRecords: List<CostRecord>
        get() = selectedCategory?.let { cat ->
            records.filter { it.category == cat }
        } ?: records

    val totalAmount: Int
        get() = filteredRecords.sumOf { it.amount }

    val monthlyTotal: Int
        get() {
            val now = LocalDate.now()
            return records
                .filter { it.date.year == now.year && it.date.month == now.month }
                .sumOf { it.amount }
        }

    val isEditing: Boolean
        get() = editingRecordId != null

    val canSave: Boolean
        get() = inputTitle.isNotBlank() && inputAmount.toIntOrNull() != null
}
