package com.segnities007.stylish_myvehicles.presentation.screen.vehiclepager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.segnities007.stylish_myvehicles.domain.model.Vehicle
import com.segnities007.stylish_myvehicles.domain.repository.CostRecordRepository
import com.segnities007.stylish_myvehicles.domain.repository.FuelRecordRepository
import com.segnities007.stylish_myvehicles.domain.repository.MaintenanceScheduleRepository
import com.segnities007.stylish_myvehicles.domain.repository.VehicleRepository
import com.segnities007.stylish_myvehicles.domain.service.DashboardCalculator
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class VehiclePagerViewModel(
    private val vehicleRepository: VehicleRepository,
    private val costRecordRepository: CostRecordRepository,
    private val fuelRecordRepository: FuelRecordRepository,
    private val maintenanceScheduleRepository: MaintenanceScheduleRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(VehiclePagerUiState())
    val uiState: StateFlow<VehiclePagerUiState> = _uiState.asStateFlow()

    private val _effects = Channel<VehiclePagerEffect>(Channel.BUFFERED)
    val effects: Flow<VehiclePagerEffect> = _effects.receiveAsFlow()

    private val dashboardJobs = mutableMapOf<Long, Job>()

    init {
        viewModelScope.launch {
            vehicleRepository.getAll().collect { vehicles ->
                _uiState.update { it.copy(vehicles = vehicles, isLoading = false) }
                syncDashboardJobs(vehicles)
            }
        }
    }

    fun accept(intent: VehiclePagerIntent) {
        when (intent) {
            is VehiclePagerIntent.AddVehicle ->
                _effects.trySend(VehiclePagerEffect.NavigateToEdit(null))

            is VehiclePagerIntent.EditVehicle ->
                _effects.trySend(VehiclePagerEffect.NavigateToEdit(intent.vehicleId))

            is VehiclePagerIntent.OpenFuel ->
                _effects.trySend(VehiclePagerEffect.NavigateToFuel(intent.vehicleId))

            is VehiclePagerIntent.OpenMaintenance ->
                _effects.trySend(VehiclePagerEffect.NavigateToMaintenance(intent.vehicleId))

            is VehiclePagerIntent.OpenCost ->
                _effects.trySend(VehiclePagerEffect.NavigateToCost(intent.vehicleId))
            is VehiclePagerIntent.OpenTrip ->
                _effects.trySend(VehiclePagerEffect.NavigateToTrip(intent.vehicleId))

            is VehiclePagerIntent.OpenVehicleDetail ->
                _effects.trySend(VehiclePagerEffect.NavigateToVehicleDetail(intent.vehicleId))

            is VehiclePagerIntent.PageChanged ->
                _uiState.update { it.copy(currentPage = intent.page) }
        }
    }

    private fun syncDashboardJobs(vehicles: List<Vehicle>) {
        val currentIds = vehicles.map { it.id }
            .toSet()
        dashboardJobs.keys.filter { it !in currentIds }
            .forEach { id ->
                dashboardJobs.remove(id)
                    ?.cancel()
                _uiState.update { it.copy(dashboardByVehicle = it.dashboardByVehicle - id) }
            }
        vehicles.forEach { vehicle ->
            if (dashboardJobs[vehicle.id] == null) {
                dashboardJobs[vehicle.id] = viewModelScope.launch {
                    combine(
                        costRecordRepository.getByVehicleId(vehicle.id),
                        fuelRecordRepository.getByVehicleId(vehicle.id),
                        maintenanceScheduleRepository.getByVehicleId(vehicle.id),
                    ) { costs, fuels, schedules ->
                        DashboardCalculator.build(costs, fuels, schedules)
                    }.collect { dashboard ->
                        _uiState.update {
                            it.copy(dashboardByVehicle = it.dashboardByVehicle + (vehicle.id to dashboard))
                        }
                    }
                }
            }
        }
    }
}

sealed interface VehiclePagerEffect {
    data class NavigateToEdit(val vehicleId: Long?) : VehiclePagerEffect
    data class NavigateToFuel(val vehicleId: Long) : VehiclePagerEffect
    data class NavigateToMaintenance(val vehicleId: Long) : VehiclePagerEffect
    data class NavigateToCost(val vehicleId: Long) : VehiclePagerEffect
    data class NavigateToTrip(val vehicleId: Long) : VehiclePagerEffect
    data class NavigateToVehicleDetail(val vehicleId: Long) : VehiclePagerEffect
}
