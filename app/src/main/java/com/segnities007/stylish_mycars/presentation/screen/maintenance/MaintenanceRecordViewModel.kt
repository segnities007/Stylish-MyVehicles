package com.segnities007.stylish_mycars.presentation.screen.maintenance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.segnities007.stylish_mycars.domain.model.MaintenanceRecord
import com.segnities007.stylish_mycars.domain.usecase.maintenance.DeleteMaintenanceRecordUseCase
import com.segnities007.stylish_mycars.domain.usecase.maintenance.GetMaintenanceRecordsUseCase
import com.segnities007.stylish_mycars.domain.usecase.maintenance.InsertMaintenanceRecordUseCase
import com.segnities007.stylish_mycars.domain.usecase.maintenance.UpdateMaintenanceRecordUseCase
import com.segnities007.stylish_mycars.domain.usecase.vehicle.GetVehicleUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MaintenanceRecordViewModel(
    private val vehicleId: Long,
    private val getMaintenanceRecordsUseCase: GetMaintenanceRecordsUseCase,
    private val insertMaintenanceRecordUseCase: InsertMaintenanceRecordUseCase,
    private val updateMaintenanceRecordUseCase: UpdateMaintenanceRecordUseCase,
    private val deleteMaintenanceRecordUseCase: DeleteMaintenanceRecordUseCase,
    private val getVehicleUseCase: GetVehicleUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(MaintenanceRecordUiState(vehicleId = vehicleId))
    val uiState: StateFlow<MaintenanceRecordUiState> = _uiState.asStateFlow()

    private val _effects = Channel<MaintenanceRecordEffect>(Channel.BUFFERED)
    val effects: Flow<MaintenanceRecordEffect> = _effects.receiveAsFlow()

    init {
        viewModelScope.launch {
            getMaintenanceRecordsUseCase(vehicleId).collect { records ->
                _uiState.update { it.copy(records = records, isLoading = false) }
            }
        }
        viewModelScope.launch {
            val vehicle = getVehicleUseCase(vehicleId).first() ?: return@launch
            val relevant = com.segnities007.stylish_mycars.domain.model.MaintenanceCategory
                .relevantFor(vehicle.category)
            _uiState.update { it.copy(relevantCategories = relevant) }
        }
    }

    fun accept(intent: MaintenanceRecordIntent) {
        when (intent) {
            is MaintenanceRecordIntent.SelectPeriod ->
                _uiState.update { it.copy(selectedPeriod = intent.period) }

            is MaintenanceRecordIntent.OpenAddDialog ->
                _uiState.update {
                    it.copy(
                        isDialogOpen = true,
                        editingRecordId = null,
                        inputDate = java.time.LocalDate.now(),
                        inputOdometer = "",
                        inputCategory = com.segnities007.stylish_mycars.domain.model.MaintenanceCategory.OTHER,
                        inputTitle = "",
                        inputCost = "",
                        inputShopName = "",
                    )
                }

            is MaintenanceRecordIntent.EditRecord -> openEditDialog(intent.recordId)
            is MaintenanceRecordIntent.CloseDialog ->
                _uiState.update { it.copy(isDialogOpen = false, editingRecordId = null) }

            is MaintenanceRecordIntent.DateChanged ->
                _uiState.update { it.copy(inputDate = intent.value) }

            is MaintenanceRecordIntent.OdometerChanged ->
                _uiState.update { it.copy(inputOdometer = intent.value.filter { c -> c.isDigit() }) }

            is MaintenanceRecordIntent.CategoryChanged ->
                _uiState.update {
                    // タイトルが未入力、またはカテゴリの自動入力そのままなら新カテゴリに合わせて切替える
                    val isDefaultTitle = it.inputTitle.isBlank() ||
                            com.segnities007.stylish_mycars.domain.model.MaintenanceCategory.entries
                                .any { cat -> cat.label == it.inputTitle }
                    it.copy(
                        inputCategory = intent.value,
                        inputTitle = if (isDefaultTitle) intent.value.label else it.inputTitle,
                    )
                }

            is MaintenanceRecordIntent.TitleChanged ->
                _uiState.update { it.copy(inputTitle = intent.value) }

            is MaintenanceRecordIntent.CostChanged ->
                _uiState.update { it.copy(inputCost = intent.value.filter { c -> c.isDigit() }) }

            is MaintenanceRecordIntent.ShopNameChanged ->
                _uiState.update { it.copy(inputShopName = intent.value) }

            is MaintenanceRecordIntent.Save -> save()
            is MaintenanceRecordIntent.RequestDelete ->
                _uiState.update { it.copy(deletingRecordId = intent.recordId) }

            is MaintenanceRecordIntent.ConfirmDelete -> confirmDelete()
            is MaintenanceRecordIntent.DismissDelete ->
                _uiState.update { it.copy(deletingRecordId = null) }

            is MaintenanceRecordIntent.NavigateBack ->
                _effects.trySend(MaintenanceRecordEffect.NavigateBack)
        }
    }

    private fun openEditDialog(recordId: Long) {
        val record = _uiState.value.records.find { it.id == recordId } ?: return
        _uiState.update {
            it.copy(
                isDialogOpen = true,
                editingRecordId = recordId,
                inputDate = record.date,
                inputOdometer = record.odometer?.toString() ?: "",
                inputCategory = record.category,
                inputTitle = record.title,
                inputCost = if (record.cost > 0) record.cost.toString() else "",
                inputShopName = record.shopName,
            )
        }
    }

    private fun save() {
        val state = _uiState.value
        if (!state.canSave) return

        viewModelScope.launch {
            if (state.isEditing) {
                val existing =
                    state.records.find { it.id == state.editingRecordId } ?: return@launch
                updateMaintenanceRecordUseCase(
                    existing.copy(
                        date = state.inputDate,
                        odometer = state.inputOdometer.toIntOrNull(),
                        category = state.inputCategory,
                        title = state.inputTitle.trim(),
                        cost = state.inputCost.toIntOrNull() ?: 0,
                        shopName = state.inputShopName.trim(),
                    ),
                )
            }
            else {
                insertMaintenanceRecordUseCase(
                    MaintenanceRecord(
                        vehicleId = vehicleId,
                        date = state.inputDate,
                        odometer = state.inputOdometer.toIntOrNull(),
                        category = state.inputCategory,
                        title = state.inputTitle.trim(),
                        cost = state.inputCost.toIntOrNull() ?: 0,
                        shopName = state.inputShopName.trim(),
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
            deleteMaintenanceRecordUseCase(record)
            _uiState.update { it.copy(deletingRecordId = null) }
        }
    }
}

sealed interface MaintenanceRecordEffect {
    data object NavigateBack : MaintenanceRecordEffect
}
