package com.segnities007.stylish_mycars.presentation.screen.cost

import com.segnities007.stylish_mycars.domain.model.CostCategory
import java.time.LocalDate

sealed interface CostListIntent {
    data class SelectCategory(val category: CostCategory?) : CostListIntent
    data object OpenAddDialog : CostListIntent
    data class EditRecord(val recordId: Long) : CostListIntent
    data object CloseDialog : CostListIntent
    data class InputCategoryChanged(val value: CostCategory) : CostListIntent
    data class InputDateChanged(val value: LocalDate) : CostListIntent
    data class InputTitleChanged(val value: String) : CostListIntent
    data class InputAmountChanged(val value: String) : CostListIntent
    data object Save : CostListIntent
    data class RequestDelete(val recordId: Long) : CostListIntent
    data object ConfirmDelete : CostListIntent
    data object DismissDelete : CostListIntent
    data object NavigateBack : CostListIntent
}
