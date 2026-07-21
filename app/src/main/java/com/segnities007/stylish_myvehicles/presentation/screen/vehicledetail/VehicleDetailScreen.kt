package com.segnities007.stylish_myvehicles.presentation.screen.vehicledetail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.segnities007.stylish_myvehicles.domain.model.Vehicle
import com.segnities007.stylish_myvehicles.domain.model.VehicleCategory
import com.segnities007.stylish_myvehicles.domain.usecase.ExportDocument
import com.segnities007.stylish_myvehicles.presentation.components.atoms.StylishIconButton
import com.segnities007.stylish_myvehicles.presentation.components.molecules.StylishConnectedCardRow
import com.segnities007.stylish_myvehicles.presentation.components.molecules.StylishConnectedListItemColumn
import com.segnities007.stylish_myvehicles.presentation.components.molecules.models.StylishConnectedCardItem
import com.segnities007.stylish_myvehicles.presentation.components.molecules.models.StylishConnectedListItem
import com.segnities007.stylish_myvehicles.presentation.components.organisms.StylishHeader
import com.segnities007.stylish_myvehicles.presentation.components.organisms.StylishScaffold
import com.segnities007.stylish_myvehicles.presentation.components.organisms.StylishSectionTitle
import com.segnities007.stylish_myvehicles.presentation.components.organisms.VehicleDeadlineSection
import com.segnities007.stylish_myvehicles.presentation.components.organisms.VehicleInfoSection
import com.segnities007.stylish_myvehicles.presentation.screen.vehicledetail.components.MaintenanceScheduleSection
import com.segnities007.stylish_myvehicles.presentation.screen.vehicledetail.components.ScheduleEditDialog
import com.segnities007.stylish_myvehicles.presentation.theme.StylishMyVehiclesTheme

@Composable
fun VehicleDetailScreen(
    viewModel: VehicleDetailViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    onNavigateToFuel: (Long) -> Unit,
    onNavigateToMaintenance: (Long) -> Unit,
    onNavigateToCost: (Long) -> Unit,
    onSaveDocument: (ExportDocument) -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is VehicleDetailEffect.NavigateBack -> onNavigateBack()
                is VehicleDetailEffect.NavigateToEdit -> onNavigateToEdit(effect.vehicleId)
                is VehicleDetailEffect.NavigateToFuel -> onNavigateToFuel(effect.vehicleId)
                is VehicleDetailEffect.NavigateToMaintenance -> onNavigateToMaintenance(effect.vehicleId)
                is VehicleDetailEffect.NavigateToCost -> onNavigateToCost(effect.vehicleId)
                is VehicleDetailEffect.SaveDocument -> onSaveDocument(effect.document)
            }
        }
    }

    StylishScaffold(
        modifier = modifier,
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            val vehicle = state.vehicle
            StylishHeader(
                modifier = Modifier.padding(horizontal = 20.dp),
                title = {
                    Text(if (vehicle != null) "${vehicle.maker} ${vehicle.name}" else "車両詳細")
                },
                navigation = {
                    StylishIconButton(
                        Icons.AutoMirrored.Filled.ArrowBack, "戻る",
                        onClick = { viewModel.accept(VehicleDetailIntent.NavigateBack) },
                    )
                },
                actions = {
                    StylishIconButton(
                        Icons.Default.Edit, "編集",
                        onClick = { viewModel.accept(VehicleDetailIntent.EditVehicle) },
                    )
                },
            )

            if (vehicle != null) {
                Column(Modifier.padding(horizontal = 20.dp)) {
                    StylishSectionTitle("記録")
                    StylishConnectedCardRow(
                        items = buildList {
                            if (vehicle.category.usesFuel) {
                                add(
                                    StylishConnectedCardItem(
                                        title = "給油",
                                        supportingText = "${state.recentFuelRecords.size}件",
                                        onClick = { viewModel.accept(VehicleDetailIntent.OpenFuelRecords) },
                                    ) {
                                        Icon(
                                            Icons.Default.LocalGasStation,
                                            null,
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    },
                                )
                            }
                            add(
                                StylishConnectedCardItem(
                                    title = "整備",
                                    supportingText = "${state.recentMaintenanceRecords.size}件",
                                    onClick = { viewModel.accept(VehicleDetailIntent.OpenMaintenanceRecords) },
                                ) {
                                    Icon(
                                        Icons.Default.Build,
                                        null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                },
                            )
                        },
                    )
                    Spacer(Modifier.height(8.dp))
                    StylishConnectedListItemColumn(
                        items = listOf(
                            StylishConnectedListItem(
                                headline = "費用",
                                supportingText = "今月: ${
                                    String.format(
                                        "%,d",
                                        state.monthlyCost
                                    )
                                }円 / 合計: ${String.format("%,d", state.totalCost)}円",
                                onClick = { viewModel.accept(VehicleDetailIntent.OpenCostList) },
                                trailingContent = {
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowForward,
                                        null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                },
                            ),
                        ),
                    )

                    // レポート
                    StylishSectionTitle("レポート")
                    StylishConnectedListItemColumn(
                        items = buildList {
                            state.averageFuelEconomy?.let {
                                add(
                                    StylishConnectedListItem(
                                        "平均燃費",
                                        "%.1f km/L".format(it),
                                        {})
                                )
                            }
                            if (state.totalDistance > 0) {
                                add(
                                    StylishConnectedListItem(
                                        "総走行距離",
                                        "${String.format("%,d", state.totalDistance)}km",
                                        {})
                                )
                            }
                            add(
                                StylishConnectedListItem(
                                    "総費用",
                                    "${String.format("%,d", state.totalCost)}円",
                                    {})
                            )
                            state.averageFuelEconomy?.let { avg ->
                                if (state.totalDistance > 0 && avg > 0) {
                                    val costPerKm = state.totalCost.toDouble() / state.totalDistance
                                    add(
                                        StylishConnectedListItem(
                                            "1kmあたりコスト",
                                            "%.1f円/km".format(costPerKm),
                                            {})
                                    )
                                }
                            }
                        },
                    )

                    // 整備スケジュール
                    if (state.schedules.isNotEmpty()) {
                        StylishSectionTitle("メンテナンス目安")
                        MaintenanceScheduleSection(
                            schedules = state.schedules,
                            onEdit = { scheduleId ->
                                viewModel.accept(VehicleDetailIntent.EditSchedule(scheduleId))
                            },
                        )
                    }

                    // 期限管理
                    StylishSectionTitle("期限管理")
                    VehicleDeadlineSection(
                        vehicle = vehicle,
                        taxPaidThisYear = state.taxPaidThisYear,
                        onEdit = { viewModel.accept(VehicleDetailIntent.EditVehicle) },
                        onTaxClick = { viewModel.accept(VehicleDetailIntent.OpenCostList) },
                    )

                    // 車両情報
                    StylishSectionTitle("車両情報")
                    VehicleInfoSection(vehicle)

                    // エクスポート
                    StylishSectionTitle("データエクスポート")
                    StylishConnectedListItemColumn(
                        items = listOf(
                            StylishConnectedListItem(
                                "給油記録をCSV出力",
                                onClick = { viewModel.accept(VehicleDetailIntent.ExportFuelCsv) },
                                trailingContent = {
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowForward,
                                        null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }),
                            StylishConnectedListItem(
                                "整備記録をCSV出力",
                                onClick = { viewModel.accept(VehicleDetailIntent.ExportMaintenanceCsv) },
                                trailingContent = {
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowForward,
                                        null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }),
                            StylishConnectedListItem(
                                "費用記録をCSV出力",
                                onClick = { viewModel.accept(VehicleDetailIntent.ExportCostCsv) },
                                trailingContent = {
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowForward,
                                        null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }),
                        ),
                    )

                    Spacer(Modifier.height(32.dp))
                }
            }
        }
    }

    if (state.isScheduleDialogOpen) {
        ScheduleEditDialog(state = state, onIntent = viewModel::accept)
    }
}

@Preview(name = "VehicleDetailScreen", showBackground = true, widthDp = 393)
@Composable
private fun VehicleDetailScreenPreview() {
    val vehicle = Vehicle(
        id = 1L,
        category = VehicleCategory.CAR,
        maker = "トヨタ",
        name = "カローラ",
        grade = "G",
        year = 2020,
        plateNumber = "横浜 300 あ 12-34",
        firstRegistrationDate = java.time.LocalDate.of(2020, 4, 1),
        inspectionExpiry = java.time.LocalDate.of(2026, 4, 30),
        insuranceExpiry = java.time.LocalDate.of(2026, 10, 1),
        insuranceCompany = "東京海上日動",
        memo = "セダン / シルバー",
    )
    StylishMyVehiclesTheme {
        Surface(Modifier.padding(20.dp)) {
            Column(Modifier.fillMaxSize()) {
                VehicleInfoSection(vehicle)
            }
        }
    }
}
