package com.segnities007.stylish_myvehicles.presentation.screen.records

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.segnities007.stylish_myvehicles.domain.model.CostCategory
import com.segnities007.stylish_myvehicles.domain.service.CostStatisticsCalculator
import com.segnities007.stylish_myvehicles.domain.usecase.cost.GetCostRecordsUseCase
import com.segnities007.stylish_myvehicles.domain.usecase.fuel.GetFuelRecordsUseCase
import com.segnities007.stylish_myvehicles.domain.usecase.maintenance.GetMaintenanceRecordsUseCase
import com.segnities007.stylish_myvehicles.domain.usecase.vehicle.GetVehiclesUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

class RecordsViewModel(
    private val vehicleId: Long,
    private val getFuelRecordsUseCase: GetFuelRecordsUseCase,
    private val getMaintenanceRecordsUseCase: GetMaintenanceRecordsUseCase,
    private val getCostRecordsUseCase: GetCostRecordsUseCase,
    private val getVehiclesUseCase: GetVehiclesUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(RecordsUiState(vehicleId = vehicleId))
    val uiState: StateFlow<RecordsUiState> = _uiState.asStateFlow()

    private val _effects = Channel<RecordsEffect>(Channel.BUFFERED)
    val effects: Flow<RecordsEffect> = _effects.receiveAsFlow()

    init {
        viewModelScope.launch {
            val vehicle = getVehiclesUseCase().first()
                .find { it.id == vehicleId }

            combine(
                getFuelRecordsUseCase(vehicleId),
                getMaintenanceRecordsUseCase(vehicleId),
                getCostRecordsUseCase(vehicleId),
            ) { fuels, maintenances, costs ->
                val allMonths = (fuels.map { YearMonth.from(it.date) } +
                        maintenances.map { YearMonth.from(it.date) } +
                        costs.map { YearMonth.from(it.date) })
                    .distinct()
                    .sortedDescending()
                    .takeIf { it.isNotEmpty() }
                    ?: listOf(YearMonth.now())

                val fuelByMonth = fuels.groupBy { YearMonth.from(it.date) }
                val maintenanceByMonth = maintenances.groupBy { YearMonth.from(it.date) }
                val costByMonth = costs.groupBy { YearMonth.from(it.date) }

                val now = LocalDate.now()
                val monthlyCost =
                    costs.filter { it.date.year == now.year && it.date.month == now.month }
                        .sumOf { it.amount }
                val yearlyCost = costs.filter { it.date.year == now.year }
                    .sumOf { it.amount }
                val totalCost = costs.sumOf { it.amount }
                val averageMonthlyCost =
                    if (costs.isNotEmpty()) CostStatisticsCalculator.averageMonthlyCost(
                        costs,
                        now
                    )
                    else null
                val economies = fuels.mapNotNull { it.fuelEconomy }
                val avgEconomy = economies.takeIf { it.isNotEmpty() }
                    ?.average()
                val totalDistance =
                    if (fuels.size >= 2) fuels.maxOf { it.odometer } - fuels.minOf { it.odometer } else 0
                val costByCategory = CostCategory.entries.mapNotNull { cat ->
                    val total = costs.filter { it.category == cat }
                        .sumOf { it.amount }
                    if (total > 0) cat to total else null
                }
                val monthlyCostTrend = (5 downTo 0).map { monthsAgo ->
                    val month = now.minusMonths(monthsAgo.toLong())
                    val total =
                        costs.filter { it.date.year == month.year && it.date.month == month.month }
                            .sumOf { it.amount }
                    "${month.monthValue}月" to total.toFloat()
                }

                RecordsUiState(
                    vehicleId = vehicleId,
                    vehicle = vehicle,
                    months = allMonths,
                    fuelByMonth = fuelByMonth,
                    maintenanceByMonth = maintenanceByMonth,
                    costByMonth = costByMonth,
                    isLoading = false,
                    currentPage = _uiState.value.currentPage.coerceIn(
                        0,
                        (allMonths.size - 1).coerceAtLeast(0)
                    ),
                    monthlyCost = monthlyCost,
                    yearlyCost = yearlyCost,
                    totalCost = totalCost,
                    averageMonthlyCost = averageMonthlyCost,
                    averageFuelEconomy = avgEconomy,
                    totalDistance = totalDistance,
                    costByCategory = costByCategory,
                    monthlyCostTrend = monthlyCostTrend,
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun accept(intent: RecordsIntent) {
        when (intent) {
            is RecordsIntent.PageChanged ->
                _uiState.update { it.copy(currentPage = intent.page) }

            is RecordsIntent.NavigateBack ->
                _effects.trySend(RecordsEffect.NavigateBack)

            is RecordsIntent.NavigateToFuel ->
                _effects.trySend(RecordsEffect.OpenFuel(vehicleId, intent.recordId))

            is RecordsIntent.NavigateToMaintenance ->
                _effects.trySend(RecordsEffect.OpenMaintenance(vehicleId, intent.recordId))

            is RecordsIntent.NavigateToCost ->
                _effects.trySend(RecordsEffect.OpenCost(vehicleId, intent.recordId))
        }
    }
}
