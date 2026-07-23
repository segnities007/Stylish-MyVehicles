package com.segnities007.stylish_myvehicles.presentation.screen.records

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private val topic: RecordTopic,
    initialPeriodMode: PeriodMode,
    private val getFuelRecordsUseCase: GetFuelRecordsUseCase,
    private val getMaintenanceRecordsUseCase: GetMaintenanceRecordsUseCase,
    private val getCostRecordsUseCase: GetCostRecordsUseCase,
    private val getVehiclesUseCase: GetVehiclesUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        RecordsUiState(vehicleId = vehicleId, topic = topic, periodMode = initialPeriodMode)
    )
    val uiState: StateFlow<RecordsUiState> = _uiState.asStateFlow()

    private val _periodMode = MutableStateFlow(initialPeriodMode)

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
                _periodMode,
            ) { fuels, maintenances, costs, mode ->
                val recordDates = when (topic) {
                    RecordTopic.FUEL -> fuels.map { it.date }
                    RecordTopic.MAINTENANCE -> maintenances.map { it.date }
                    RecordTopic.COST -> costs.map { it.date }
                }
                val periods = computePeriods(mode, recordDates, LocalDate.now())

                RecordsUiState(
                    vehicleId = vehicleId,
                    topic = topic,
                    periodMode = mode,
                    vehicle = vehicle,
                    periods = periods,
                    fuelRecords = fuels,
                    maintenanceRecords = maintenances,
                    costRecords = costs,
                    isLoading = false,
                    currentPage = _uiState.value.currentPage.coerceIn(
                        0,
                        (periods.size - 1).coerceAtLeast(0)
                    ),
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

            is RecordsIntent.ChangePeriodMode ->
                _periodMode.value = intent.mode

            is RecordsIntent.NavigateBack ->
                _effects.trySend(RecordsEffect.NavigateBack)
        }
    }
}

internal fun continuousMonths(recordMonths: List<YearMonth>, now: YearMonth): List<YearMonth> {
    val newest = listOfNotNull(now, recordMonths.maxOrNull()).max()
    val oldest = listOfNotNull(now, recordMonths.minOrNull()).min()
    // 記録が今月のみの場合でも前月までページを作り、横スワイプで過去月（記録なし）を閲覧できるようにする
    val lowerBound = now.minusMonths(1)
    val boundedOldest = if (oldest.isAfter(lowerBound)) lowerBound else oldest
    // 昇順（古い月→新しい月）。画面側で新しい月が右側に来る
    return buildList {
        var month = boundedOldest
        while (!month.isAfter(newest)) {
            add(month)
            month = month.plusMonths(1)
        }
    }
}
