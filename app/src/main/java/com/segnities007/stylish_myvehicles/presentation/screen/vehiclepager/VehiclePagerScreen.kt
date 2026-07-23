package com.segnities007.stylish_myvehicles.presentation.screen.vehiclepager

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.segnities007.stylish_myvehicles.domain.model.CostCategory
import com.segnities007.stylish_myvehicles.domain.model.Vehicle
import com.segnities007.stylish_myvehicles.domain.model.VehicleCategory
import com.segnities007.stylish_myvehicles.presentation.components.atoms.StylishIconButton
import com.segnities007.stylish_myvehicles.presentation.components.molecules.BarChartData
import com.segnities007.stylish_myvehicles.presentation.components.molecules.BarChartSegment
import com.segnities007.stylish_myvehicles.presentation.components.molecules.LineChartData
import com.segnities007.stylish_myvehicles.presentation.components.molecules.PieChartData
import com.segnities007.stylish_myvehicles.presentation.components.molecules.StylishConnectedCardGrid
import com.segnities007.stylish_myvehicles.presentation.components.molecules.StylishDialogSurface
import com.segnities007.stylish_myvehicles.presentation.components.molecules.costCategoryColor
import com.segnities007.stylish_myvehicles.presentation.components.molecules.models.StylishConnectedCardItem
import com.segnities007.stylish_myvehicles.presentation.components.organisms.BarChartSection
import com.segnities007.stylish_myvehicles.presentation.components.organisms.LineChartSection
import com.segnities007.stylish_myvehicles.presentation.components.organisms.PieChartSection
import com.segnities007.stylish_myvehicles.presentation.components.organisms.LocalBottomBarVisible
import com.segnities007.stylish_myvehicles.presentation.components.organisms.StylishHeader
import com.segnities007.stylish_myvehicles.presentation.components.organisms.StylishBottomBar
import com.segnities007.stylish_myvehicles.presentation.components.organisms.StylishPageContent
import com.segnities007.stylish_myvehicles.presentation.components.organisms.StylishScaffold
import com.segnities007.stylish_myvehicles.presentation.components.organisms.StylishSectionTitle
import com.segnities007.stylish_myvehicles.presentation.screen.vehiclepager.components.PagerIndicator
import com.segnities007.stylish_myvehicles.presentation.screen.vehiclepager.components.UrgentAlertCard
import com.segnities007.stylish_myvehicles.presentation.theme.StylishMyVehiclesTheme

@Composable
fun VehiclePagerScreen(
    viewModel: VehiclePagerViewModel,
    onNavigateToEdit: (Long?) -> Unit,
    onNavigateToFuel: (Long) -> Unit,
    onNavigateToMaintenance: (Long) -> Unit,
    onNavigateToCost: (Long) -> Unit,
    onNavigateToVehicleDetail: (Long) -> Unit,
    onNavigateToNotifications: () -> Unit,
    onAddFuel: (Long) -> Unit,
    onAddMaintenance: (Long) -> Unit,
    onAddCost: (Long) -> Unit,
    bottomBarVisible: MutableState<Boolean>? = null,
    showAddDialog: MutableState<Boolean>? = null,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is VehiclePagerEffect.NavigateToEdit -> onNavigateToEdit(effect.vehicleId)
                is VehiclePagerEffect.NavigateToFuel -> onNavigateToFuel(effect.vehicleId)
                is VehiclePagerEffect.NavigateToMaintenance -> onNavigateToMaintenance(effect.vehicleId)
                is VehiclePagerEffect.NavigateToCost -> onNavigateToCost(effect.vehicleId)
                is VehiclePagerEffect.NavigateToVehicleDetail -> onNavigateToVehicleDetail(effect.vehicleId)
            }
        }
    }

    val pageCount = state.vehicles.size + 1
    val pagerState = rememberPagerState(pageCount = { pageCount })
    val pageListStates = remember(pageCount) { List(pageCount) { LazyListState() } }
    val localShowAddDialog = remember { mutableStateOf(false) }
    val addDialogState = showAddDialog ?: localShowAddDialog
    var isAddDialogVisible by remember { mutableStateOf(false) }
    LaunchedEffect(addDialogState.value) {
        if (addDialogState.value) {
            isAddDialogVisible = true
            addDialogState.value = false
        }
    }

    val currentListState = pageListStates[pagerState.currentPage]
    val isAtTop by remember(currentListState) {
        derivedStateOf {
            currentListState.firstVisibleItemIndex == 0 &&
                    currentListState.firstVisibleItemScrollOffset <= 0
        }
    }
    LaunchedEffect(isAtTop) {
        bottomBarVisible?.value = isAtTop
    }

    LaunchedEffect(pagerState.currentPage) {
        viewModel.accept(VehiclePagerIntent.PageChanged(pagerState.currentPage))
    }

    StylishScaffold(modifier = modifier) {
        Box(Modifier.fillMaxSize()) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
            ) { page ->
                if (page < state.vehicles.size) {
                    val vehicle = state.vehicles[page]
                    VehiclePage(
                        vehicle = vehicle,
                        dashboard = state.dashboardFor(vehicle.id),
                        onIntent = viewModel::accept,
                        listState = pageListStates[page],
                    )
                }
                else {
                    AddVehiclePage(onAdd = { viewModel.accept(VehiclePagerIntent.AddVehicle) })
                }
            }

            if (state.vehicles.size > 1) {
                PagerIndicator(
                    pagerState = pagerState,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .navigationBarsPadding()
                        .padding(bottom = 88.dp),
                )
            }
        }
    }

    if (isAddDialogVisible) {
        val currentVehicle = state.vehicles.getOrNull(pagerState.currentPage)
        StylishDialogSurface(onDismiss = { isAddDialogVisible = false }) {
            Column(Modifier.padding(24.dp)) {
                Text("記録を追加", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(16.dp))
                StylishConnectedCardGrid(
                    columns = 2,
                    spacing = 4.dp,
                    items = listOf(
                        StylishConnectedCardItem(
                            title = "給油",
                            onClick = {
                                isAddDialogVisible = false
                                if (currentVehicle != null) onAddFuel(currentVehicle.id)
                            },
                            trailingContent = {
                                Icon(
                                    Icons.Default.LocalGasStation,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            },
                        ),
                        StylishConnectedCardItem(
                            title = "整備",
                            onClick = {
                                isAddDialogVisible = false
                                if (currentVehicle != null) onAddMaintenance(currentVehicle.id)
                            },
                            trailingContent = {
                                Icon(
                                    Icons.Default.Build,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            },
                        ),
                        StylishConnectedCardItem(
                            title = "費用",
                            onClick = {
                                isAddDialogVisible = false
                                if (currentVehicle != null) onAddCost(currentVehicle.id)
                            },
                            trailingContent = {
                                Icon(
                                    Icons.Default.AttachMoney,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            },
                        ),
                    ),
                )
            }
        }
    }
}

@Preview(name = "VehiclePagerScreen", showBackground = true, widthDp = 393)
@Composable
private fun VehiclePagerScreenPreview() {
    val vehicle = Vehicle(
        id = 1L,
        category = VehicleCategory.CAR,
        maker = "トヨタ",
        name = "カローラ",
        grade = "G",
        year = 2020,
        plateNumber = "横浜 300 あ 12-34",
    )
    val dashboard = VehicleDashboard(
        monthlyCost = 25000,
        yearlyCost = 300000,
        totalCost = 1500000,
        averageMonthlyCost = 25000.0,
        averageFuelEconomy = 15.5,
        totalDistance = 45000,
        nextMaintenanceLabel = "オイル交換",
        nextMaintenanceDays = 30,
        costByCategory = listOf(
            CostCategory.FUEL to 80000,
            CostCategory.MAINTENANCE to 30000,
            CostCategory.INSURANCE to 20000,
        ),
        monthlyCostTrend = listOf(
            "1月" to 25000f,
            "2月" to 28000f,
            "3月" to 22000f,
        ),
        fuelEconomyTrend = listOf(
            "1月" to 14.5f,
            "2月" to 15.0f,
            "3月" to 16.2f,
        ),
    )
    StylishMyVehiclesTheme {
        Surface(Modifier.padding(20.dp)) {
            VehiclePage(vehicle = vehicle, dashboard = dashboard, onIntent = {})
        }
    }
}

@Preview(name = "VehiclePage", showBackground = true, widthDp = 393)
@Composable
private fun VehiclePagePreview() {
    val vehicle = Vehicle(
        id = 2L,
        category = VehicleCategory.CAR,
        maker = "ホンダ",
        name = "フィット",
        grade = "RS",
        year = 2021,
        plateNumber = "品川 500 い 56-78",
    )
    val dashboard = VehicleDashboard(
        monthlyCost = 18000,
        yearlyCost = 220000,
        totalCost = 800000,
        averageMonthlyCost = 18000.0,
        averageFuelEconomy = 18.2,
        totalDistance = 28000,
        nextMaintenanceLabel = "タイヤ交換",
        nextMaintenanceDays = 90,
        costByCategory = listOf(
            CostCategory.FUEL to 50000,
            CostCategory.MAINTENANCE to 15000,
            CostCategory.INSURANCE to 18000,
            CostCategory.TAX to 5000,
        ),
        monthlyCostTrend = listOf(
            "1月" to 18000f,
            "2月" to 19000f,
            "3月" to 17500f,
        ),
        fuelEconomyTrend = listOf(
            "1月" to 17.8f,
            "2月" to 18.5f,
            "3月" to 18.0f,
        ),
    )
    StylishMyVehiclesTheme {
        Surface(Modifier.padding(20.dp)) {
            VehiclePage(vehicle = vehicle, dashboard = dashboard, onIntent = {})
        }
    }
}

@Preview(name = "AddVehiclePage", showBackground = true, widthDp = 393)
@Composable
private fun AddVehiclePagePreview() {
    StylishMyVehiclesTheme {
        Surface(Modifier.padding(20.dp)) {
            AddVehiclePage(onAdd = {})
        }
    }
}

@Composable
private fun VehiclePage(
    vehicle: Vehicle,
    dashboard: VehicleDashboard,
    onIntent: (VehiclePagerIntent) -> Unit,
    listState: LazyListState = LazyListState(),
) {
    StylishPageContent(
        listState = listState,
        header = {
            StylishHeader(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(vehicle.name)
                        if (vehicle.plateNumber.isNotBlank()) {
                            Text(
                                vehicle.plateNumber,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                },
                navigation = {
                    StylishIconButton(
                        Icons.Default.Edit, "編集",
                        onClick = { onIntent(VehiclePagerIntent.EditVehicle(vehicle.id)) },
                    )
                },
            )
        },
        content = {
            item {
                if (dashboard.fuelEconomyTrend.size >= 2) {
                    LineChartSection(
                        title = "燃費推移 (km/L)",
                        data = dashboard.fuelEconomyTrend.map {
                            LineChartData(it.first, it.second)
                        },
                    )
                    Spacer(Modifier.height(16.dp))
                }
                PieChartSection(
                    title = "費用カテゴリ",
                    data = dashboard.costByCategory.map { (category, total) ->
                        PieChartData(
                            category.label,
                            total.toFloat(),
                            costCategoryColor(category.ordinal)
                        )
                    },
                )
                Spacer(Modifier.height(16.dp))
                BarChartSection(
                    title = "月次費用",
                    data = dashboard.monthlyCostByCategory.map { slice ->
                        BarChartData(
                            label = slice.label,
                            value = slice.total.toFloat(),
                            segments = slice.byCategory.map { (category, amount) ->
                                BarChartSegment(
                                    amount.toFloat(),
                                    costCategoryColor(category.ordinal),
                                )
                            },
                        )
                    },
                )
                Spacer(Modifier.height(16.dp))
            }

            item {
                UrgentAlertCard(vehicle = vehicle)
                Spacer(Modifier.height(16.dp))
            }

            item {
                StylishSectionTitle("記録")
                StylishConnectedCardGrid(
                    columns = 2,
                    items = listOf(
                        StylishConnectedCardItem(
                            title = "給油",
                            supportingText = dashboard.averageFuelEconomy?.let {
                                "平均 %.1f km/L".format(it)
                            } ?: "記録・燃費",
                            onClick = { onIntent(VehiclePagerIntent.OpenFuel(vehicle.id)) },
                        ) {
                            Icon(
                                Icons.Default.LocalGasStation,
                                null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        StylishConnectedCardItem(
                            title = "整備",
                            supportingText = "記録・目安",
                            onClick = { onIntent(VehiclePagerIntent.OpenMaintenance(vehicle.id)) },
                        ) {
                            Icon(
                                Icons.Default.Build,
                                null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        StylishConnectedCardItem(
                            title = "費用",
                            supportingText = if (dashboard.monthlyCost > 0) {
                                "今月 ${String.format("%,d", dashboard.monthlyCost)}円"
                            } else {
                                "一覧・グラフ"
                            },
                            onClick = { onIntent(VehiclePagerIntent.OpenCost(vehicle.id)) },
                        ) {
                            Icon(
                                Icons.Default.AttachMoney,
                                null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                    ),
                )
            }

            item {
                Spacer(Modifier.height(16.dp))
                StylishConnectedCardGrid(
                    columns = 2,
                    items = listOf(
                        StylishConnectedCardItem(
                            title = "車両情報",
                            supportingText = vehicle.currentInspectionExpiry?.let {
                                "車検: $it"
                            } ?: "車検・諸元・保険",
                            onClick = { onIntent(VehiclePagerIntent.OpenVehicleDetail(vehicle.id)) },
                        ),
                    ),
                )
                Spacer(Modifier.height(40.dp))
            }
        },
    )
}

@Composable
private fun AddVehiclePage(onAdd: () -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 32.dp),
        ) {
            Icon(
                Icons.Default.DirectionsCar,
                contentDescription = null,
                modifier = Modifier
                    .size(72.dp)
                    .padding(bottom = 16.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                "車両を追加しましょう",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(12.dp))
            Text(
                "車検・保険・税金の期限管理、給油記録、整備履歴を一元管理できます",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = onAdd,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onSurface,
                    contentColor = MaterialTheme.colorScheme.surface,
                ),
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier
                    .height(48.dp)
                    .width(200.dp),
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("車両を登録する")
            }
        }
    }
}
