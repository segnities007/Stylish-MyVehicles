package com.segnities007.stylish_mycars.presentation.screen.vehiclepager

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.segnities007.stylish_mycars.domain.model.CostCategory
import com.segnities007.stylish_mycars.domain.model.Vehicle
import com.segnities007.stylish_mycars.domain.model.VehicleCategory
import com.segnities007.stylish_mycars.presentation.components.atoms.StylishIconButton
import com.segnities007.stylish_mycars.presentation.components.molecules.BarChartData
import com.segnities007.stylish_mycars.presentation.components.molecules.PieChartData
import com.segnities007.stylish_mycars.presentation.components.molecules.StylishConnectedCardGrid
import com.segnities007.stylish_mycars.presentation.components.molecules.costCategoryColor
import com.segnities007.stylish_mycars.presentation.components.molecules.models.StylishConnectedCardItem
import com.segnities007.stylish_mycars.presentation.components.organisms.BarChartSection
import com.segnities007.stylish_mycars.presentation.components.organisms.PieChartSection
import com.segnities007.stylish_mycars.presentation.components.organisms.StylishHeader
import com.segnities007.stylish_mycars.presentation.components.organisms.StylishPageContent
import com.segnities007.stylish_mycars.presentation.components.organisms.StylishScaffold
import com.segnities007.stylish_mycars.presentation.components.organisms.StylishSectionTitle
import com.segnities007.stylish_mycars.presentation.screen.vehiclepager.components.DashboardStatsGrid
import com.segnities007.stylish_mycars.presentation.screen.vehiclepager.components.UrgentAlertCard
import com.segnities007.stylish_mycars.presentation.theme.StylishMyCarsTheme

@Composable
fun VehiclePagerScreen(
    viewModel: VehiclePagerViewModel,
    onNavigateToEdit: (Long?) -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToFuel: (Long) -> Unit,
    onNavigateToMaintenance: (Long) -> Unit,
    onNavigateToCost: (Long) -> Unit,
    onNavigateToVehicleDetail: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is VehiclePagerEffect.NavigateToEdit -> onNavigateToEdit(effect.vehicleId)
                is VehiclePagerEffect.NavigateToSettings -> onNavigateToSettings()
                is VehiclePagerEffect.NavigateToFuel -> onNavigateToFuel(effect.vehicleId)
                is VehiclePagerEffect.NavigateToMaintenance -> onNavigateToMaintenance(effect.vehicleId)
                is VehiclePagerEffect.NavigateToCost -> onNavigateToCost(effect.vehicleId)
                is VehiclePagerEffect.NavigateToVehicleDetail -> onNavigateToVehicleDetail(effect.vehicleId)
            }
        }
    }

    val pageCount = state.vehicles.size + 1
    val pagerState = rememberPagerState(pageCount = { pageCount })

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
                    )
                }
                else {
                    AddVehiclePage(onAdd = { viewModel.accept(VehiclePagerIntent.AddVehicle) })
                }
            }

            if (pageCount > 1) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .navigationBarsPadding(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    repeat(pageCount) { index ->
                        val isSelected = index == pagerState.currentPage
                        Box(
                            Modifier
                                .padding(horizontal = 3.dp)
                                .size(if (isSelected) 8.dp else 6.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.onSurface
                                    else MaterialTheme.colorScheme.outlineVariant
                                ),
                        )
                    }
                }
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
    StylishMyCarsTheme {
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
    StylishMyCarsTheme {
        Surface(Modifier.padding(20.dp)) {
            VehiclePage(vehicle = vehicle, dashboard = dashboard, onIntent = {})
        }
    }
}

@Preview(name = "AddVehiclePage", showBackground = true, widthDp = 393)
@Composable
private fun AddVehiclePagePreview() {
    StylishMyCarsTheme {
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
) {
    StylishPageContent(
        header = {
            StylishHeader(
                title = { Text(vehicle.name) },
                navigation = {
                    StylishIconButton(
                        Icons.Default.Edit, "編集",
                        onClick = { onIntent(VehiclePagerIntent.EditVehicle(vehicle.id)) },
                    )
                },
                actions = {
                    StylishIconButton(
                        Icons.Default.Settings, "設定",
                        onClick = { onIntent(VehiclePagerIntent.OpenSettings) },
                    )
                },
            )
        },
    ) {
        item {
            UrgentAlertCard(vehicle, dashboard)
            Spacer(Modifier.height(16.dp))
        }

        item {
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
                data = dashboard.monthlyCostTrend.map { BarChartData(it.first, it.second) },
            )
            Spacer(Modifier.height(16.dp))
        }

        item {
            StylishSectionTitle("記録")
            StylishConnectedCardGrid(
                columns = 2,
                items = listOf(
                    StylishConnectedCardItem(
                        title = "給油",
                        supportingText = "記録・燃費",
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
                        supportingText = "一覧・グラフ",
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
                        supportingText = "車検・諸元・保険",
                        onClick = { onIntent(VehiclePagerIntent.OpenVehicleDetail(vehicle.id)) },
                    ),
                ),
            )
            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun AddVehiclePage(onAdd: () -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
