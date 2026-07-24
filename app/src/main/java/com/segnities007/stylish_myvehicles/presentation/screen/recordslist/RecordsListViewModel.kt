package com.segnities007.stylish_myvehicles.presentation.screen.recordslist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.segnities007.stylish_myvehicles.domain.model.CostCategory
import com.segnities007.stylish_myvehicles.domain.repository.CostRecordRepository
import com.segnities007.stylish_myvehicles.domain.repository.FuelRecordRepository
import com.segnities007.stylish_myvehicles.domain.repository.MaintenanceRecordRepository
import com.segnities007.stylish_myvehicles.domain.repository.TripRecordRepository
import com.segnities007.stylish_myvehicles.domain.repository.VehicleRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.time.YearMonth

/**
 * 全車両の給油・整備・費用を集約し、時系列（月ごとのセクション）で提供するViewModel。
 * 車両一覧を監視し、車両ごとに3記録のFlowをまとめてから全車両分を合流させる。
 */
@OptIn(ExperimentalCoroutinesApi::class)
class RecordsListViewModel(
    vehicleRepository: VehicleRepository,
    fuelRecordRepository: FuelRecordRepository,
    maintenanceRecordRepository: MaintenanceRecordRepository,
    costRecordRepository: CostRecordRepository,
    tripRecordRepository: TripRecordRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(RecordsListUiState())
    val uiState: StateFlow<RecordsListUiState> = _uiState.asStateFlow()

    private val _effects = Channel<RecordsListEffect>(Channel.BUFFERED)
    val effects: Flow<RecordsListEffect> = _effects.receiveAsFlow()

    init {
        viewModelScope.launch {
            vehicleRepository.getAll()
                .flatMapLatest { vehicles ->
                    val perVehicleFlows: List<Flow<List<RecordEntry>>> = vehicles.map { vehicle ->
                        val vehicleName = "${vehicle.maker} ${vehicle.name}".trim()
                        combine(
                            fuelRecordRepository.getByVehicleId(vehicle.id),
                            maintenanceRecordRepository.getByVehicleId(vehicle.id),
                            costRecordRepository.getByVehicleId(vehicle.id),
                            tripRecordRepository.getByVehicleId(vehicle.id),
                        ) { fuels, maintenances, costs, trips ->
                            buildList<RecordEntry> {
                                fuels.forEach { add(FuelEntry(it, vehicleName)) }
                                maintenances.forEach { add(MaintenanceEntry(it, vehicleName)) }
                                // FUEL/MAINTENANCEカテゴリのCostRecordは給油・整備で自動作成されるため除外（二重計上防止）
                                costs.filter {
                                    it.category != CostCategory.FUEL &&
                                            it.category != CostCategory.MAINTENANCE
                                }.forEach { add(CostEntry(it, vehicleName)) }
                                trips.filterNot { it.isRecording }
                                    .forEach { add(TripEntry(it, vehicleName)) }
                            }
                        }
                    }
                    if (perVehicleFlows.isEmpty()) flowOf(emptyList())
                    else combine(perVehicleFlows) { lists -> lists.flatMap { it } }
                }
                .collect { entries ->
                    val sections = entries
                        .groupBy { YearMonth.from(it.date) }
                        .toSortedMap(compareByDescending { it })
                        .map { (month, list) ->
                            RecordSection(month, list.sortedByDescending { it.date })
                        }
                    _uiState.value = RecordsListUiState(
                        isLoading = false,
                        sections = sections,
                    )
                }
        }
    }

    fun accept(intent: RecordsListIntent) {
        when (intent) {
            is RecordsListIntent.NavigateBack ->
                _effects.trySend(RecordsListEffect.NavigateBack)

            is RecordsListIntent.OpenRecord -> when (val entry = intent.entry) {
                is FuelEntry -> _effects.trySend(RecordsListEffect.OpenFuel(entry.record.vehicleId))
                is MaintenanceEntry ->
                    _effects.trySend(RecordsListEffect.OpenMaintenance(entry.record.vehicleId))
                is CostEntry -> _effects.trySend(RecordsListEffect.OpenCost(entry.record.vehicleId))
                is TripEntry -> _effects.trySend(RecordsListEffect.OpenTrip(entry.record.vehicleId))
            }
        }
    }
}
