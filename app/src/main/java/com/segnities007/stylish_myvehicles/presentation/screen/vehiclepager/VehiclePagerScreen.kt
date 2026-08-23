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
import androidx.compose.material.icons.filled.Route
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.segnities007.stylish_myvehicles.R
import com.segnities007.stylish_myvehicles.domain.model.CostCategory
import com.segnities007.stylish_myvehicles.domain.model.Vehicle
import com.segnities007.stylish_myvehicles.domain.model.VehicleCategory
import com.segnities007.stylishui.components.atoms.StylishDialogSurface
import com.segnities007.stylishui.components.atoms.StylishIconButton
import com.segnities007.stylishui.components.atoms.StylishSectionTitle
import com.segnities007.stylishui.components.charts.BarChartData
import com.segnities007.stylishui.components.charts.BarChartSegment
import com.segnities007.stylishui.components.charts.LineChartData
import com.segnities007.stylishui.components.charts.PieChartData
import com.segnities007.stylishui.components.charts.stylishChartColor
import com.segnities007.stylishui.components.molecules.StylishConnectedCardGrid
import com.segnities007.stylishui.components.models.StylishConnectedCardItem
import com.segnities007.stylishui.components.patterns.BarChartSection
import com.segnities007.stylish_myvehicles.presentation.components.organisms.AddRecordDialog
import com.segnities007.stylishui.components.patterns.LineChartSection
import com.segnities007.stylish_myvehicles.presentation.components.organisms.PieChartSection
import com.segnities007.stylish_myvehicles.presentation.components.organisms.LocalBottomBarVisible
import com.segnities007.stylishui.components.patterns.StylishHeader
import com.segnities007.stylish_myvehicles.presentation.components.organisms.StylishBottomBar
import com.segnities007.stylishui.components.patterns.StylishPageContent
import com.segnities007.stylishui.components.patterns.StylishScaffold
import com.segnities007.stylish_myvehicles.presentation.screen.vehiclepager.components.PagerIndicator
import com.segnities007.stylish_myvehicles.presentation.theme.StylishMyVehiclesTheme

@Composable
fun VehiclePagerScreen(
    viewModel: VehiclePagerViewModel,
    onNavigateToEdit: (Long?) -> Unit,
    onNavigateToFuel: (Long) -> Unit,
    onNavigateToMaintenance: (Long) -> Unit,
    onNavigateToCost: (Long) -> Unit,
    onNavigateToTrip: (Long) -> Unit,
    onNavigateToVehicleDetail: (Long) -> Unit,
    onNavigateToNotifications: () -> Unit,
    onAddFuel: (Long) -> Unit,
    onAddMaintenance: (Long) -> Unit,
    onAddCost: (Long) -> Unit,
    onAddTrip: (Long) -> Unit,
    bottomBarVisible: MutableState<Boolean>? = null,
    showAddDialog: MutableState<Boolean>? = null,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is VehiclePagerEffect.NavigateToEdit -> onNavigateToEdit(effect.vehicleId)
                is VehiclePagerEffect.NavigateToFuel -> onNavigateToFuel(effect.vehicleId)
                is VehiclePagerEffect.NavigateToMaintenance -> onNavigateToMaintenance(effect.vehicleId)
                is VehiclePagerEffect.NavigateToCost -> onNavigateToCost(effect.vehicleId)
                is VehiclePagerEffect.NavigateToTrip -> onNavigateToTrip(effect.vehicleId)
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
        AddRecordDialog(
            vehicleId = state.vehicles.getOrNull(pagerState.currentPage)?.id,
            onDismiss = { isAddDialogVisible = false },
            onAddFuel = onAddFuel,
            onAddMaintenance = onAddMaintenance,
            onAddCost = onAddCost,
            onAddTrip = onAddTrip,
        )
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
                        Icons.Default.Edit, stringResource(R.string.edit),
                        onClick = { onIntent(VehiclePagerIntent.EditVehicle(vehicle.id)) },
                    )
                },
            )
        },
        content = {
            item {
                if (dashboard.fuelEconomyTrend.size >= 2) {
                    LineChartSection(
                        title = stringResource(R.string.fuel_economy_trend),
                        data = dashboard.fuelEconomyTrend.map {
                            LineChartData(it.first, it.second)
                        },
                        contentDescriptionPrefix = stringResource(R.string.line_chart),
                        emptyLabel = stringResource(R.string.no_data),
                    )
                    Spacer(Modifier.height(16.dp))
                }
                PieChartSection(
                    title = stringResource(R.string.cost_category),
                    data = dashboard.costByCategory.map { (category, total) ->
                        PieChartData(
                            category.label,
                            total.toFloat(),
                            stylishChartColor(category.ordinal)
                        )
                    },
                )
                Spacer(Modifier.height(16.dp))
                BarChartSection(
                    title = stringResource(R.string.monthly_cost),
                    contentDescriptionPrefix = stringResource(R.string.bar_chart),
                    emptyLabel = stringResource(R.string.no_data),
                    data = dashboard.monthlyCostByCategory.map { slice ->
                        BarChartData(
                            label = slice.label,
                            value = slice.total.toFloat(),
                            segments = slice.byCategory.map { (category, amount) ->
                                BarChartSegment(
                                    amount.toFloat(),
                                    stylishChartColor(category.ordinal),
                                )
                            },
                        )
                    },
                )
                Spacer(Modifier.height(16.dp))
            }

            item {
                StylishSectionTitle(stringResource(R.string.records_section))
                StylishConnectedCardGrid(
                    columns = 2,
                    items = listOf(
                        StylishConnectedCardItem(
                            title = stringResource(R.string.fuel_label),
                            onClick = { onIntent(VehiclePagerIntent.OpenFuel(vehicle.id)) },
                            trailingContent = {
                                Icon(Icons.Default.LocalGasStation, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            },
                        ),
                        StylishConnectedCardItem(
                            title = stringResource(R.string.maintenance_label),
                            onClick = { onIntent(VehiclePagerIntent.OpenMaintenance(vehicle.id)) },
                            trailingContent = {
                                Icon(Icons.Default.Build, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            },
                        ),
                        StylishConnectedCardItem(
                            title = stringResource(R.string.cost_label),
                            onClick = { onIntent(VehiclePagerIntent.OpenCost(vehicle.id)) },
                            trailingContent = {
                                Icon(Icons.Default.AttachMoney, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            },
                        ),
                        StylishConnectedCardItem(
                            title = stringResource(R.string.trip_label),
                            onClick = { onIntent(VehiclePagerIntent.OpenTrip(vehicle.id)) },
                            trailingContent = {
                                Icon(Icons.Default.Route, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            },
                        ),
                    ),
                )
            }

            item {
                Spacer(Modifier.height(16.dp))
                StylishConnectedCardGrid(
                    columns = 2,
                    items = listOf(
                        StylishConnectedCardItem(
                            title = stringResource(R.string.vehicle_info_label),
                            onClick = { onIntent(VehiclePagerIntent.OpenVehicleDetail(vehicle.id)) },
                            trailingContent = {
                                Icon(Icons.Default.DirectionsCar, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            },
                        ),
                    ),
                )
                Spacer(Modifier.height(40.dp))
            }
            item {
                Spacer(Modifier.height(20.dp))
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
                stringResource(R.string.add_vehicle_prompt),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(12.dp))
            Text(
                stringResource(R.string.add_vehicle_description),
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
                Text(stringResource(R.string.register_vehicle))
            }
        }
    }
}
