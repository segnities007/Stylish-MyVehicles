package com.segnities007.stylish_myvehicles.presentation.screen.recordslist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.segnities007.stylish_myvehicles.domain.usecase.cost.GetCostRecordsUseCase
import com.segnities007.stylish_myvehicles.domain.usecase.fuel.GetFuelRecordsUseCase
import com.segnities007.stylish_myvehicles.domain.usecase.maintenance.GetMaintenanceRecordsUseCase
import com.segnities007.stylish_myvehicles.domain.usecase.vehicle.GetVehicleUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.time.YearMonth

class RecordsListViewModel(
    private val vehicleId: Long,
    getVehicleUseCase: GetVehicleUseCase,
    getFuelRecordsUseCase: GetFuelRecordsUseCase,
    getMaintenanceRecordsUseCase: GetMaintenanceRecordsUseCase,
    getCostRecordsUseCase: GetCostRecordsUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(RecordsListUiState(vehicleId = vehicleId))
    val uiState: StateFlow<RecordsListUiState> = _uiState.asStateFlow()

    private val _effects = Channel<RecordsListEffect>(Channel.BUFFERED)
    val effects: Flow<RecordsListEffect> = _effects.receiveAsFlow()

    init {
        viewModelScope.launch {
            combine(
                getVehicleUseCase(vehicleId),
                getFuelRecordsUseCase(vehicleId),
                getMaintenanceRecordsUseCase(vehicleId),
                getCostRecordsUseCase(vehicleId),
            ) { vehicle, fuels, maintenances, costs ->
                val entries: List<RecordEntry> = buildList {
                    fuels.forEach { add(FuelEntry(it)) }
                    maintenances.forEach { add(MaintenanceEntry(it)) }
                    costs.forEach { add(CostEntry(it)) }
                }
                val sections = entries
                    .groupBy { YearMonth.from(it.date) }
                    .toSortedMap(compareByDescending { it })
                    .map { (month, list) ->
                        RecordSection(month, list.sortedByDescending { it.date })
                    }
                RecordsListUiState(
                    vehicleId = vehicleId,
                    vehicleName = vehicle?.let { "${it.maker} ${it.name}" } ?: "",
                    isLoading = false,
                    sections = sections,
                )
            }.collect { state -> _uiState.value = state }
        }
    }

    fun accept(intent: RecordsListIntent) {
        when (intent) {
            is RecordsListIntent.NavigateBack ->
                _effects.trySend(RecordsListEffect.NavigateBack)

            is RecordsListIntent.OpenRecord -> when (intent.entry) {
                is FuelEntry -> _effects.trySend(RecordsListEffect.OpenFuel(vehicleId))
                is MaintenanceEntry -> _effects.trySend(RecordsListEffect.OpenMaintenance(vehicleId))
                is CostEntry -> _effects.trySend(RecordsListEffect.OpenCost(vehicleId))
            }
        }
    }
}
