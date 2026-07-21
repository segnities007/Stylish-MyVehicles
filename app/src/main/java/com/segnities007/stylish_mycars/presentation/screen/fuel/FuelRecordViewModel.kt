package com.segnities007.stylish_mycars.presentation.screen.fuel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.segnities007.stylish_mycars.domain.model.FuelRecord
import com.segnities007.stylish_mycars.domain.service.FuelEconomyCalculator
import com.segnities007.stylish_mycars.domain.usecase.fuel.DeleteFuelRecordUseCase
import com.segnities007.stylish_mycars.domain.usecase.fuel.GetFuelRecordsUseCase
import com.segnities007.stylish_mycars.domain.usecase.fuel.InsertFuelRecordUseCase
import com.segnities007.stylish_mycars.domain.usecase.fuel.UpdateFuelRecordUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FuelRecordViewModel(
    private val vehicleId: Long,
    private val getFuelRecordsUseCase: GetFuelRecordsUseCase,
    private val insertFuelRecordUseCase: InsertFuelRecordUseCase,
    private val updateFuelRecordUseCase: UpdateFuelRecordUseCase,
    private val deleteFuelRecordUseCase: DeleteFuelRecordUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(FuelRecordUiState(vehicleId = vehicleId))
    val uiState: StateFlow<FuelRecordUiState> = _uiState.asStateFlow()

    private val _effects = Channel<FuelRecordEffect>(Channel.BUFFERED)
    val effects: Flow<FuelRecordEffect> = _effects.receiveAsFlow()

    init {
        viewModelScope.launch {
            getFuelRecordsUseCase(vehicleId).collect { records ->
                _uiState.update { it.copy(records = records, isLoading = false) }
            }
        }
    }

    fun accept(intent: FuelRecordIntent) {
        when (intent) {
            is FuelRecordIntent.SelectPeriod ->
                _uiState.update { it.copy(selectedPeriod = intent.period) }
            is FuelRecordIntent.OpenAddDialog -> openAddDialog()
            is FuelRecordIntent.EditRecord -> openEditDialog(intent.recordId)
            is FuelRecordIntent.CloseDialog ->
                _uiState.update {
                    it.copy(isDialogOpen = false, editingRecordId = null, calculatedEconomy = null)
                }
            is FuelRecordIntent.DateChanged ->
                _uiState.update { it.copy(inputDate = intent.value) }
            is FuelRecordIntent.OdometerChanged -> {
                _uiState.update { it.copy(inputOdometer = intent.value.filter { c -> c.isDigit() }) }
                recalculateEconomy()
            }
            is FuelRecordIntent.VolumeChanged -> {
                _uiState.update { it.copy(inputVolume = intent.value.filter { c -> c.isDigit() || c == '.' }) }
                recalculateEconomy()
            }
            is FuelRecordIntent.AmountChanged ->
                _uiState.update { it.copy(inputAmount = intent.value.filter { c -> c.isDigit() }) }
            is FuelRecordIntent.FullTankChanged ->
                _uiState.update { it.copy(inputIsFullTank = intent.value) }
            is FuelRecordIntent.MemoChanged ->
                _uiState.update { it.copy(inputMemo = intent.value) }
            is FuelRecordIntent.ReceiptScanned -> {
                _uiState.update {
                    it.copy(
                        inputVolume = intent.volume ?: it.inputVolume,
                        inputAmount = intent.amount ?: it.inputAmount,
                        inputOdometer = intent.odometer ?: it.inputOdometer,
                    )
                }
                recalculateEconomy()
            }
            is FuelRecordIntent.ScanningChanged ->
                _uiState.update { it.copy(isScanning = intent.value) }
            is FuelRecordIntent.Save -> save()
            is FuelRecordIntent.RequestDelete ->
                _uiState.update { it.copy(deletingRecordId = intent.recordId) }
            is FuelRecordIntent.ConfirmDelete -> confirmDelete()
            is FuelRecordIntent.DismissDelete ->
                _uiState.update { it.copy(deletingRecordId = null) }
            is FuelRecordIntent.NavigateBack ->
                _effects.trySend(FuelRecordEffect.NavigateBack)
        }
    }

    private fun openAddDialog() {
        val latest = _uiState.value.records.firstOrNull()
        _uiState.update {
            it.copy(
                isDialogOpen = true,
                editingRecordId = null,
                inputDate = java.time.LocalDate.now(),
                inputOdometer = latest?.odometer?.toString() ?: "",
                inputVolume = "",
                inputAmount = "",
                inputIsFullTank = true,
                inputMemo = "",
                calculatedEconomy = null,
            )
        }
    }

    private fun openEditDialog(recordId: Long) {
        val record = _uiState.value.records.find { it.id == recordId } ?: return
        _uiState.update {
            it.copy(
                isDialogOpen = true,
                editingRecordId = recordId,
                inputDate = record.date,
                inputOdometer = record.odometer.toString(),
                inputVolume = record.volume.toString(),
                inputAmount = record.amount.toString(),
                inputIsFullTank = record.isFullTank,
                inputMemo = record.memo,
                calculatedEconomy = record.fuelEconomy?.let { e -> "%.1f km/L".format(e) },
            )
        }
    }

    private fun recalculateEconomy() {
        val state = _uiState.value
        val currentOdo = state.inputOdometer.toIntOrNull() ?: return
        val volume = state.inputVolume.toDoubleOrNull() ?: return

        val prevRecord = if (state.isEditing) {
            state.records
                .filter { it.id != state.editingRecordId }
                .firstOrNull { it.odometer < currentOdo }
        } else {
            state.records.firstOrNull()
        }

        if (!state.inputIsFullTank || prevRecord == null || !prevRecord.isFullTank) {
            _uiState.update { it.copy(calculatedEconomy = null) }
            return
        }
        val economy = FuelEconomyCalculator.calculate(currentOdo, prevRecord.odometer, volume)
        _uiState.update {
            it.copy(calculatedEconomy = economy?.let { e -> "%.1f km/L".format(e) })
        }
    }

    private fun save() {
        val state = _uiState.value
        if (!state.canSave) return

        viewModelScope.launch {
            val currentOdo = state.inputOdometer.toInt()
            val volume = state.inputVolume.toDouble()
            val amount = state.inputAmount.toInt()

            val prevRecord = if (state.isEditing) {
                state.records
                    .filter { it.id != state.editingRecordId }
                    .firstOrNull { it.odometer < currentOdo }
            } else {
                state.records.firstOrNull()
            }

            val economy = if (state.inputIsFullTank && prevRecord != null && prevRecord.isFullTank) {
                FuelEconomyCalculator.calculate(currentOdo, prevRecord.odometer, volume)
            } else null

            val unitPrice = if (volume > 0) (amount / volume).toInt() else null

            if (state.isEditing) {
                val existing = state.records.find { it.id == state.editingRecordId } ?: return@launch
                updateFuelRecordUseCase(
                    existing.copy(
                        date = state.inputDate,
                        odometer = currentOdo,
                        volume = volume,
                        amount = amount,
                        unitPrice = unitPrice,
                        fuelEconomy = economy,
                        isFullTank = state.inputIsFullTank,
                        memo = state.inputMemo,
                    ),
                )
            } else {
                insertFuelRecordUseCase(
                    FuelRecord(
                        vehicleId = vehicleId,
                        date = state.inputDate,
                        odometer = currentOdo,
                        volume = volume,
                        amount = amount,
                        unitPrice = unitPrice,
                        fuelEconomy = economy,
                        isFullTank = state.inputIsFullTank,
                        memo = state.inputMemo,
                    ),
                )
            }
            _uiState.update {
                it.copy(isDialogOpen = false, editingRecordId = null, calculatedEconomy = null)
            }
        }
    }

    private fun confirmDelete() {
        val recordId = _uiState.value.deletingRecordId ?: return
        viewModelScope.launch {
            val record = _uiState.value.records.find { it.id == recordId } ?: return@launch
            deleteFuelRecordUseCase(record)
            _uiState.update { it.copy(deletingRecordId = null) }
        }
    }
}

sealed interface FuelRecordEffect {
    data object NavigateBack : FuelRecordEffect
}
