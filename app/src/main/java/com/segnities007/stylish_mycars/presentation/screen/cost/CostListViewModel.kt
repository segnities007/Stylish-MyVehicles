package com.segnities007.stylish_mycars.presentation.screen.cost

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.segnities007.stylish_mycars.domain.model.CostCategory
import com.segnities007.stylish_mycars.domain.model.CostRecord
import com.segnities007.stylish_mycars.domain.usecase.cost.DeleteCostRecordUseCase
import com.segnities007.stylish_mycars.domain.usecase.cost.GetCostRecordsUseCase
import com.segnities007.stylish_mycars.domain.usecase.cost.InsertCostRecordUseCase
import com.segnities007.stylish_mycars.domain.usecase.cost.UpdateCostRecordUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CostListViewModel(
    private val vehicleId: Long,
    private val getCostRecordsUseCase: GetCostRecordsUseCase,
    private val insertCostRecordUseCase: InsertCostRecordUseCase,
    private val updateCostRecordUseCase: UpdateCostRecordUseCase,
    private val deleteCostRecordUseCase: DeleteCostRecordUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(CostListUiState(vehicleId = vehicleId))
    val uiState: StateFlow<CostListUiState> = _uiState.asStateFlow()

    private val _effects = Channel<CostListEffect>(Channel.BUFFERED)
    val effects: Flow<CostListEffect> = _effects.receiveAsFlow()

    init {
        viewModelScope.launch {
            getCostRecordsUseCase(vehicleId).collect { records ->
                _uiState.update { it.copy(records = records, isLoading = false) }
            }
        }
    }

    fun accept(intent: CostListIntent) {
        when (intent) {
            is CostListIntent.SelectCategory ->
                _uiState.update { it.copy(selectedCategory = intent.category) }
            is CostListIntent.OpenAddDialog -> openAddDialog()
            is CostListIntent.EditRecord -> openEditDialog(intent.recordId)
            is CostListIntent.CloseDialog ->
                _uiState.update { it.copy(isDialogOpen = false, editingRecordId = null) }
            is CostListIntent.InputCategoryChanged ->
                _uiState.update {
                    it.copy(
                        inputCategory = intent.value,
                        inputTitle = it.inputTitle.ifBlank { intent.value.label },
                    )
                }
            is CostListIntent.InputDateChanged ->
                _uiState.update { it.copy(inputDate = intent.value) }
            is CostListIntent.InputTitleChanged ->
                _uiState.update { it.copy(inputTitle = intent.value) }
            is CostListIntent.InputAmountChanged ->
                _uiState.update { it.copy(inputAmount = intent.value.filter { c -> c.isDigit() }) }
            is CostListIntent.InputMemoChanged ->
                _uiState.update { it.copy(inputMemo = intent.value) }
            is CostListIntent.Save -> save()
            is CostListIntent.RequestDelete ->
                _uiState.update { it.copy(deletingRecordId = intent.recordId) }
            is CostListIntent.ConfirmDelete -> confirmDelete()
            is CostListIntent.DismissDelete ->
                _uiState.update { it.copy(deletingRecordId = null) }
            is CostListIntent.NavigateBack ->
                _effects.trySend(CostListEffect.NavigateBack)
        }
    }

    private fun openAddDialog() {
        _uiState.update {
            it.copy(
                isDialogOpen = true,
                editingRecordId = null,
                inputCategory = CostCategory.OTHER,
                inputDate = java.time.LocalDate.now(),
                inputTitle = "",
                inputAmount = "",
                inputMemo = "",
            )
        }
    }

    private fun openEditDialog(recordId: Long) {
        val record = _uiState.value.records.find { it.id == recordId } ?: return
        _uiState.update {
            it.copy(
                isDialogOpen = true,
                editingRecordId = recordId,
                inputCategory = record.category,
                inputDate = record.date,
                inputTitle = record.title,
                inputAmount = record.amount.toString(),
                inputMemo = record.memo,
            )
        }
    }

    private fun save() {
        val state = _uiState.value
        if (!state.canSave) return

        viewModelScope.launch {
            if (state.isEditing) {
                val existing = state.records.find { it.id == state.editingRecordId } ?: return@launch
                updateCostRecordUseCase(
                    existing.copy(
                        date = state.inputDate,
                        category = state.inputCategory,
                        title = state.inputTitle.trim(),
                        amount = state.inputAmount.toInt(),
                        memo = state.inputMemo.trim(),
                    ),
                )
            } else {
                insertCostRecordUseCase(
                    CostRecord(
                        vehicleId = vehicleId,
                        date = state.inputDate,
                        category = state.inputCategory,
                        title = state.inputTitle.trim(),
                        amount = state.inputAmount.toInt(),
                        memo = state.inputMemo.trim(),
                    ),
                )
            }
            _uiState.update { it.copy(isDialogOpen = false, editingRecordId = null) }
        }
    }

    private fun confirmDelete() {
        val recordId = _uiState.value.deletingRecordId ?: return
        viewModelScope.launch {
            val record = _uiState.value.records.find { it.id == recordId } ?: return@launch
            deleteCostRecordUseCase(record)
            _uiState.update { it.copy(deletingRecordId = null) }
        }
    }
}

sealed interface CostListEffect {
    data object NavigateBack : CostListEffect
}
