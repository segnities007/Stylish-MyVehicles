package com.segnities007.stylish_mycars.presentation.screen.vehiclepager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.segnities007.stylish_mycars.domain.model.CostRecord
import com.segnities007.stylish_mycars.domain.model.FuelRecord
import com.segnities007.stylish_mycars.domain.model.MaintenanceSchedule
import com.segnities007.stylish_mycars.domain.model.Vehicle
import com.segnities007.stylish_mycars.domain.usecase.cost.GetCostRecordsUseCase
import com.segnities007.stylish_mycars.domain.usecase.fuel.GetFuelRecordsUseCase
import com.segnities007.stylish_mycars.domain.usecase.maintenance.GetMaintenanceSchedulesUseCase
import com.segnities007.stylish_mycars.domain.usecase.vehicle.GetVehiclesUseCase
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
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class VehiclePagerViewModel(
    private val getVehiclesUseCase: GetVehiclesUseCase,
    private val getCostRecordsUseCase: GetCostRecordsUseCase,
    private val getFuelRecordsUseCase: GetFuelRecordsUseCase,
    private val getMaintenanceSchedulesUseCase: GetMaintenanceSchedulesUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(VehiclePagerUiState())
    val uiState: StateFlow<VehiclePagerUiState> = _uiState.asStateFlow()

    private val _effects = Channel<VehiclePagerEffect>(Channel.BUFFERED)
    val effects: Flow<VehiclePagerEffect> = _effects.receiveAsFlow()

    private val dashboardJobs = mutableMapOf<Long, Job>()

    init {
        viewModelScope.launch {
            getVehiclesUseCase().collect { vehicles ->
                _uiState.update { it.copy(vehicles = vehicles, isLoading = false) }
                syncDashboardJobs(vehicles)
            }
        }
    }

    fun accept(intent: VehiclePagerIntent) {
        when (intent) {
            is VehiclePagerIntent.AddVehicle ->
                _effects.trySend(VehiclePagerEffect.NavigateToEdit(null))

            is VehiclePagerIntent.OpenSettings ->
                _effects.trySend(VehiclePagerEffect.NavigateToSettings)

            is VehiclePagerIntent.EditVehicle ->
                _effects.trySend(VehiclePagerEffect.NavigateToEdit(intent.vehicleId))

            is VehiclePagerIntent.OpenFuel ->
                _effects.trySend(VehiclePagerEffect.NavigateToFuel(intent.vehicleId))

            is VehiclePagerIntent.OpenMaintenance ->
                _effects.trySend(VehiclePagerEffect.NavigateToMaintenance(intent.vehicleId))

            is VehiclePagerIntent.OpenCost ->
                _effects.trySend(VehiclePagerEffect.NavigateToCost(intent.vehicleId))

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
                        getCostRecordsUseCase(vehicle.id),
                        getFuelRecordsUseCase(vehicle.id),
                        getMaintenanceSchedulesUseCase(vehicle.id),
                    ) { costs, fuels, schedules ->
                        buildDashboard(costs, fuels, schedules)
                    }.collect { dashboard ->
                        _uiState.update {
                            it.copy(dashboardByVehicle = it.dashboardByVehicle + (vehicle.id to dashboard))
                        }
                    }
                }
            }
        }
    }

    private fun buildDashboard(
        costs: List<CostRecord>,
        fuels: List<FuelRecord>,
        schedules: List<MaintenanceSchedule>,
    ): VehicleDashboard {
        val now = LocalDate.now()
        val monthlyCost = costs
            .filter { it.date.year == now.year && it.date.month == now.month }
            .sumOf { it.amount }
        val yearlyCost = costs.filter { it.date.year == now.year }
            .sumOf { it.amount }
        val totalCost = costs.sumOf { it.amount }

        val economies = fuels.mapNotNull { it.fuelEconomy }
        val avgEconomy = economies.takeIf { it.isNotEmpty() }
            ?.average()
        val totalDistance = if (fuels.size >= 2) {
            fuels.maxOf { it.odometer } - fuels.minOf { it.odometer }
        }
        else 0

        val nextMaintenance = schedules.mapNotNull { schedule ->
            val days = daysUntilScheduleDue(schedule, now)
            if (days != null) schedule.category.label to days else null
        }
            .minByOrNull { it.second }

        val costByCategory = com.segnities007.stylish_mycars.domain.model.CostCategory.entries
            .mapNotNull { cat ->
                val total = costs.filter { it.category == cat }
                    .sumOf { it.amount }
                if (total > 0) cat to total else null
            }

        val monthlyCostTrend = (5 downTo 0).map { monthsAgo ->
            val month = now.minusMonths(monthsAgo.toLong())
            val total = costs
                .filter { it.date.year == month.year && it.date.month == month.month }
                .sumOf { it.amount }
            "${month.monthValue}月" to total.toFloat()
        }

        val fuelEconomyTrend = fuels
            .filter { it.fuelEconomy != null }
            .take(20)
            .reversed()
            .map { r -> "${r.date.monthValue}/${r.date.dayOfMonth}" to r.fuelEconomy!!.toFloat() }

        return VehicleDashboard(
            monthlyCost = monthlyCost,
            yearlyCost = yearlyCost,
            totalCost = totalCost,
            averageMonthlyCost = com.segnities007.stylish_mycars.domain.service.CostStatisticsCalculator
                .averageMonthlyCost(costs, now),
            averageFuelEconomy = avgEconomy,
            totalDistance = totalDistance,
            nextMaintenanceLabel = nextMaintenance?.first,
            nextMaintenanceDays = nextMaintenance?.second,
            costByCategory = costByCategory,
            monthlyCostTrend = monthlyCostTrend,
            fuelEconomyTrend = fuelEconomyTrend,
        )
    }

    private fun daysUntilScheduleDue(schedule: MaintenanceSchedule, now: LocalDate): Long? {
        val interval = schedule.intervalMonths ?: return null
        val lastDone = schedule.lastDoneDate ?: return null
        return ChronoUnit.DAYS.between(now, lastDone.plusMonths(interval.toLong()))
    }
}

sealed interface VehiclePagerEffect {
    data class NavigateToEdit(val vehicleId: Long?) : VehiclePagerEffect
    data object NavigateToSettings : VehiclePagerEffect
    data class NavigateToFuel(val vehicleId: Long) : VehiclePagerEffect
    data class NavigateToMaintenance(val vehicleId: Long) : VehiclePagerEffect
    data class NavigateToCost(val vehicleId: Long) : VehiclePagerEffect
    data class NavigateToVehicleDetail(val vehicleId: Long) : VehiclePagerEffect
}
