package com.segnities007.stylish_myvehicles.presentation.screen.maintenance

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.segnities007.stylish_myvehicles.domain.model.MaintenanceCategory
import com.segnities007.stylish_myvehicles.domain.model.MaintenanceRecord
import com.segnities007.stylish_myvehicles.domain.model.CostRecord
import com.segnities007.stylish_myvehicles.domain.model.MaintenanceSchedule
import com.segnities007.stylish_myvehicles.domain.model.RecordPeriod
import com.segnities007.stylish_myvehicles.domain.model.Vehicle
import com.segnities007.stylish_myvehicles.domain.model.VehicleCategory
import com.segnities007.stylish_myvehicles.domain.repository.MaintenanceRecordRepository
import com.segnities007.stylish_myvehicles.domain.repository.CostRecordRepository
import com.segnities007.stylish_myvehicles.domain.repository.MaintenanceScheduleRepository
import com.segnities007.stylish_myvehicles.domain.repository.VehicleRepository
import com.segnities007.stylish_myvehicles.domain.usecase.maintenance.DeleteMaintenanceRecordUseCase
import com.segnities007.stylish_myvehicles.domain.usecase.maintenance.GetMaintenanceRecordsUseCase
import com.segnities007.stylish_myvehicles.domain.usecase.maintenance.InsertMaintenanceRecordUseCase
import com.segnities007.stylish_myvehicles.domain.usecase.maintenance.UpdateMaintenanceRecordUseCase
import com.segnities007.stylish_myvehicles.domain.usecase.vehicle.GetVehicleUseCase
import com.segnities007.stylish_myvehicles.presentation.components.atoms.StylishIconButton
import com.segnities007.stylish_myvehicles.presentation.components.molecules.StylishConnectedChipRow
import com.segnities007.stylish_myvehicles.presentation.components.molecules.StylishConnectedListItemColumn
import com.segnities007.stylish_myvehicles.presentation.components.molecules.StylishDeleteConfirmDialog
import com.segnities007.stylish_myvehicles.presentation.components.molecules.StylishEmptyState
import com.segnities007.stylish_myvehicles.presentation.components.molecules.models.StylishConnectedChipItem
import com.segnities007.stylish_myvehicles.presentation.components.molecules.models.StylishConnectedListItem
import com.segnities007.stylish_myvehicles.presentation.components.organisms.StylishHeader
import com.segnities007.stylish_myvehicles.presentation.components.organisms.StylishScaffold
import com.segnities007.stylish_myvehicles.presentation.screen.maintenance.components.MaintenanceInputDialog
import com.segnities007.stylish_myvehicles.presentation.theme.StylishMyVehiclesTheme
import kotlinx.coroutines.flow.flowOf
import java.time.LocalDate

@Composable
fun MaintenanceRecordScreen(
    viewModel: MaintenanceRecordViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is MaintenanceRecordEffect.NavigateBack -> onNavigateBack()
            }
        }
    }

    StylishScaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.accept(MaintenanceRecordIntent.OpenAddDialog) },
                containerColor = MaterialTheme.colorScheme.onSurface,
                contentColor = MaterialTheme.colorScheme.surface,
            ) {
                Icon(Icons.Default.Add, contentDescription = "整備を記録")
            }
        },
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            StylishHeader(
                modifier = Modifier.padding(horizontal = 20.dp),
                title = { Text("整備記録") },
                navigation = {
                    StylishIconButton(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        "戻る",
                        onClick = { viewModel.accept(MaintenanceRecordIntent.NavigateBack) },
                    )
                },
            )

            // 期間フィルタ
            StylishConnectedChipRow(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                fillWidth = true,
                items = RecordPeriod.entries.map { period ->
                    StylishConnectedChipItem(
                        label = period.label,
                        onClick = { viewModel.accept(MaintenanceRecordIntent.SelectPeriod(period)) },
                        selected = state.selectedPeriod == period,
                    )
                },
            )

            val visibleRecords = state.filteredRecords

            when {
                state.isLoading ->
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }

                state.records.isEmpty() ->
                    StylishEmptyState(
                        icon = Icons.Default.Build,
                        title = "整備記録がありません",
                        description = "下の＋ボタンから整備を記録しましょう",
                    )

                visibleRecords.isEmpty() ->
                    StylishEmptyState(
                        icon = Icons.Default.Build,
                        title = "この期間の記録がありません",
                        description = "期間フィルタを変更してみてください",
                    )

                else ->
                    StylishConnectedListItemColumn(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        spacing = 4.dp,
                        items = visibleRecords.map { record ->
                            StylishConnectedListItem(
                                headline = record.title,
                                supportingText = buildSubtitle(record),
                                onClick = {
                                    viewModel.accept(
                                        MaintenanceRecordIntent.EditRecord(
                                            record.id
                                        )
                                    )
                                },
                                onLongClick = {
                                    viewModel.accept(MaintenanceRecordIntent.RequestDelete(record.id))
                                },
                                trailingContent = {
                                    Text(
                                        record.category.label,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                },
                            )
                        },
                    )
            }
        }
    }

    if (state.isDialogOpen) {
        MaintenanceInputDialog(
            state = state,
            onIntent = viewModel::accept,
        )
    }

    if (state.deletingRecordId != null) {
        StylishDeleteConfirmDialog(
            title = "整備記録を削除",
            message = "この整備記録を削除しますか？この操作は取り消せません。",
            onConfirm = { viewModel.accept(MaintenanceRecordIntent.ConfirmDelete) },
            onDismiss = { viewModel.accept(MaintenanceRecordIntent.DismissDelete) },
        )
    }
}

private fun buildSubtitle(record: MaintenanceRecord): String {
    val parts = mutableListOf(record.date.toString())
    record.odometer?.let { parts.add("${String.format("%,d", it)}km") }
    if (record.cost > 0) parts.add("${String.format("%,d", record.cost)}円")
    record.shopName.takeIf { it.isNotBlank() }
        ?.let { parts.add(it) }
    return parts.joinToString(" / ")
}

@Preview(name = "MaintenanceRecordScreen", showBackground = true, widthDp = 393)
@Composable
private fun MaintenanceRecordScreenPreview() {
    StylishMyVehiclesTheme {
        Surface(Modifier.padding(20.dp)) {
            val repository = remember {
                object : MaintenanceRecordRepository {
                    override fun getByVehicleId(vehicleId: Long) = flowOf(
                        listOf(
                            MaintenanceRecord(
                                1,
                                1,
                                LocalDate.of(2026, 7, 10),
                                50000,
                                MaintenanceCategory.OIL,
                                "エンジンオイル交換",
                                5000,
                                "カーショップA"
                            ),
                            MaintenanceRecord(
                                2,
                                1,
                                LocalDate.of(2026, 6, 15),
                                49000,
                                MaintenanceCategory.TIRE,
                                "タイヤ交換",
                                40000,
                                "タイヤ館"
                            ),
                        ),
                    )

                    override suspend fun insert(record: MaintenanceRecord) = 0L
                    override suspend fun update(record: MaintenanceRecord) {}
                    override suspend fun delete(record: MaintenanceRecord) {}
                }
            }
            val vehicleRepository = remember {
                object : VehicleRepository {
                    override fun getAll() = flowOf<List<Vehicle>>(emptyList())
                    override fun getById(id: Long) = flowOf<Vehicle?>(
                        Vehicle(1, VehicleCategory.CAR, "トヨタ", "プリウス", "Z", 2022),
                    )

                    override suspend fun insert(vehicle: Vehicle) = 0L
                    override suspend fun update(vehicle: Vehicle) {}
                    override suspend fun delete(vehicle: Vehicle) {}
                }
            }
            val costRepository = remember {
                object : CostRecordRepository {
                    override fun getByVehicleId(vehicleId: Long) = flowOf<List<CostRecord>>(emptyList())
                    override fun getByVehicleIdAndDateRange(
                        vehicleId: Long, start: LocalDate, end: LocalDate
                    ) = flowOf<List<CostRecord>>(emptyList())
                    override suspend fun insert(record: com.segnities007.stylish_myvehicles.domain.model.CostRecord) = 0L
                    override suspend fun update(record: com.segnities007.stylish_myvehicles.domain.model.CostRecord) {}
                    override suspend fun delete(record: com.segnities007.stylish_myvehicles.domain.model.CostRecord) {}
                }
            }
            val scheduleRepository = remember {
                object : MaintenanceScheduleRepository {
                    override fun getByVehicleId(vehicleId: Long) = flowOf<List<MaintenanceSchedule>>(emptyList())
                    override suspend fun getAll() = emptyList<MaintenanceSchedule>()
                    override suspend fun insertDefaults(vehicleId: Long, category: VehicleCategory) {}
                    override suspend fun update(schedule: MaintenanceSchedule) {}
                    override suspend fun updateLastDone(vehicleId: Long, category: String, date: LocalDate, odometer: Int?) {}
                }
            }
            val viewModel = remember {
                MaintenanceRecordViewModel(
                    vehicleId = 1L,
                    getMaintenanceRecordsUseCase = GetMaintenanceRecordsUseCase(repository),
                    insertMaintenanceRecordUseCase = InsertMaintenanceRecordUseCase(
                        repository, costRepository, scheduleRepository
                    ),
                    updateMaintenanceRecordUseCase = UpdateMaintenanceRecordUseCase(repository),
                    deleteMaintenanceRecordUseCase = DeleteMaintenanceRecordUseCase(repository),
                    getVehicleUseCase = GetVehicleUseCase(vehicleRepository),
                )
            }
            MaintenanceRecordScreen(viewModel = viewModel, onNavigateBack = {})
        }
    }
}
