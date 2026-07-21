package com.segnities007.stylish_myvehicles.presentation.screen.vehicledetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.segnities007.stylish_myvehicles.domain.usecase.ExportDataUseCase
import com.segnities007.stylish_myvehicles.domain.usecase.cost.GetCostRecordsUseCase
import com.segnities007.stylish_myvehicles.domain.usecase.fuel.GetFuelRecordsUseCase
import com.segnities007.stylish_myvehicles.domain.usecase.maintenance.GetMaintenanceRecordsUseCase
import com.segnities007.stylish_myvehicles.domain.usecase.maintenance.GetMaintenanceSchedulesUseCase
import com.segnities007.stylish_myvehicles.domain.usecase.maintenance.UpdateMaintenanceScheduleUseCase
import com.segnities007.stylish_myvehicles.domain.usecase.vehicle.GetVehicleUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class VehicleDetailViewModel(
    private val vehicleId: Long,
    private val getVehicleUseCase: GetVehicleUseCase,
    private val getFuelRecordsUseCase: GetFuelRecordsUseCase,
    private val getMaintenanceRecordsUseCase: GetMaintenanceRecordsUseCase,
    private val getCostRecordsUseCase: GetCostRecordsUseCase,
    private val getMaintenanceSchedulesUseCase: GetMaintenanceSchedulesUseCase,
    private val updateMaintenanceScheduleUseCase: UpdateMaintenanceScheduleUseCase,
    private val exportDataUseCase: ExportDataUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(VehicleDetailUiState())
    val uiState: StateFlow<VehicleDetailUiState> = _uiState.asStateFlow()

    private val _effects = Channel<VehicleDetailEffect>(Channel.BUFFERED)
    val effects: Flow<VehicleDetailEffect> = _effects.receiveAsFlow()

    init {
        viewModelScope.launch {
            getVehicleUseCase(vehicleId).collect { vehicle ->
                _uiState.update { it.copy(vehicle = vehicle, isLoading = false) }
            }
        }
        viewModelScope.launch {
            getFuelRecordsUseCase(vehicleId).collect { records ->
                val economies = records.mapNotNull { it.fuelEconomy }
                val avg = economies.takeIf { it.isNotEmpty() }
                    ?.average()
                val totalDist = if (records.size >= 2) {
                    records.maxOf { it.odometer } - records.minOf { it.odometer }
                }
                else 0
                _uiState.update {
                    it.copy(
                        recentFuelRecords = records.take(3),
                        averageFuelEconomy = avg,
                        totalDistance = totalDist,
                    )
                }
            }
        }
        viewModelScope.launch {
            getMaintenanceRecordsUseCase(vehicleId).collect { records ->
                _uiState.update { it.copy(recentMaintenanceRecords = records.take(3)) }
            }
        }
        viewModelScope.launch {
            getCostRecordsUseCase(vehicleId).collect { records ->
                val now = java.time.LocalDate.now()
                val monthly = records
                    .filter { it.date.year == now.year && it.date.month == now.month }
                    .sumOf { it.amount }
                val total = records.sumOf { it.amount }
                _uiState.update { it.copy(monthlyCost = monthly, totalCost = total) }
            }
        }
        viewModelScope.launch {
            getMaintenanceSchedulesUseCase(vehicleId).collect { schedules ->
                _uiState.update { it.copy(schedules = schedules) }
            }
        }
    }

    fun accept(intent: VehicleDetailIntent) {
        when (intent) {
            is VehicleDetailIntent.NavigateBack ->
                _effects.trySend(VehicleDetailEffect.NavigateBack)

            is VehicleDetailIntent.EditVehicle ->
                _effects.trySend(VehicleDetailEffect.NavigateToEdit(vehicleId))

            is VehicleDetailIntent.OpenFuelRecords ->
                _effects.trySend(VehicleDetailEffect.NavigateToFuel(vehicleId))

            is VehicleDetailIntent.OpenMaintenanceRecords ->
                _effects.trySend(VehicleDetailEffect.NavigateToMaintenance(vehicleId))

            is VehicleDetailIntent.OpenCostList ->
                _effects.trySend(VehicleDetailEffect.NavigateToCost(vehicleId))

            is VehicleDetailIntent.ExportFuelCsv -> exportFuel()
            is VehicleDetailIntent.ExportMaintenanceCsv -> exportMaintenance()
            is VehicleDetailIntent.ExportCostCsv -> exportCost()
            is VehicleDetailIntent.EditSchedule -> openScheduleDialog(intent.scheduleId)
            is VehicleDetailIntent.CloseScheduleDialog ->
                _uiState.update { it.copy(isScheduleDialogOpen = false, editingScheduleId = null) }

            is VehicleDetailIntent.ScheduleIntervalKmChanged ->
                _uiState.update { it.copy(scheduleInputKm = intent.value.filter { c -> c.isDigit() }) }

            is VehicleDetailIntent.ScheduleIntervalMonthsChanged ->
                _uiState.update { it.copy(scheduleInputMonths = intent.value.filter { c -> c.isDigit() }) }

            is VehicleDetailIntent.ScheduleLastDoneDateChanged ->
                _uiState.update { it.copy(scheduleInputLastDoneDate = intent.value) }

            is VehicleDetailIntent.ScheduleLastDoneOdometerChanged ->
                _uiState.update { it.copy(scheduleInputLastDoneOdo = intent.value.filter { c -> c.isDigit() }) }

            is VehicleDetailIntent.SaveSchedule -> saveSchedule()
        }
    }

    private fun openScheduleDialog(scheduleId: Long) {
        val schedule = _uiState.value.schedules.find { it.id == scheduleId } ?: return
        _uiState.update {
            it.copy(
                isScheduleDialogOpen = true,
                editingScheduleId = scheduleId,
                scheduleInputKm = schedule.intervalKm?.toString() ?: "",
                scheduleInputMonths = schedule.intervalMonths?.toString() ?: "",
                scheduleInputLastDoneDate = schedule.lastDoneDate,
                scheduleInputLastDoneOdo = schedule.lastDoneOdometer?.toString() ?: "",
            )
        }
    }

    private fun saveSchedule() {
        val state = _uiState.value
        val scheduleId = state.editingScheduleId ?: return
        val existing = state.schedules.find { it.id == scheduleId } ?: return

        viewModelScope.launch {
            updateMaintenanceScheduleUseCase(
                existing.copy(
                    intervalKm = state.scheduleInputKm.toIntOrNull(),
                    intervalMonths = state.scheduleInputMonths.toIntOrNull(),
                    lastDoneDate = state.scheduleInputLastDoneDate,
                    lastDoneOdometer = state.scheduleInputLastDoneOdo.toIntOrNull(),
                ),
            )
            _uiState.update { it.copy(isScheduleDialogOpen = false, editingScheduleId = null) }
        }
    }

    private fun exportFuel() {
        viewModelScope.launch {
            val records = getFuelRecordsUseCase(vehicleId).first()
            _effects.send(
                VehicleDetailEffect.SaveDocument(
                    exportDataUseCase.exportFuelRecordsCsv(
                        records
                    )
                )
            )
        }
    }

    private fun exportMaintenance() {
        viewModelScope.launch {
            val records = getMaintenanceRecordsUseCase(vehicleId).first()
            _effects.send(
                VehicleDetailEffect.SaveDocument(
                    exportDataUseCase.exportMaintenanceRecordsCsv(
                        records
                    )
                )
            )
        }
    }

    private fun exportCost() {
        viewModelScope.launch {
            val records = getCostRecordsUseCase(vehicleId).first()
            _effects.send(
                VehicleDetailEffect.SaveDocument(
                    exportDataUseCase.exportCostRecordsCsv(
                        records
                    )
                )
            )
        }
    }
}

sealed interface VehicleDetailEffect {
    data object NavigateBack : VehicleDetailEffect
    data class NavigateToEdit(val vehicleId: Long) : VehicleDetailEffect
    data class NavigateToFuel(val vehicleId: Long) : VehicleDetailEffect
    data class NavigateToMaintenance(val vehicleId: Long) : VehicleDetailEffect
    data class NavigateToCost(val vehicleId: Long) : VehicleDetailEffect
    data class SaveDocument(val document: com.segnities007.stylish_myvehicles.domain.usecase.ExportDocument) :
        VehicleDetailEffect
}
