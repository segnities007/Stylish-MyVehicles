package com.segnities007.stylish_mycars.presentation.screen.vehiclepager

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.segnities007.stylish_mycars.presentation.components.molecules.StylishConnectedCardRow
import com.segnities007.stylish_mycars.presentation.components.molecules.StylishConnectedListItemColumn
import com.segnities007.stylish_mycars.presentation.components.molecules.StylishEmptyState
import com.segnities007.stylish_mycars.presentation.components.molecules.models.StylishConnectedCardItem
import com.segnities007.stylish_mycars.presentation.components.molecules.models.StylishConnectedListItem
import com.segnities007.stylish_mycars.presentation.components.organisms.StylishHeader
import com.segnities007.stylish_mycars.presentation.components.organisms.StylishSectionTitle
import com.segnities007.stylish_mycars.presentation.components.organisms.VehicleDeadlineSection
import com.segnities007.stylish_mycars.presentation.components.organisms.VehicleInfoSection
import com.segnities007.stylish_mycars.presentation.components.atoms.StylishIconButton
import com.segnities007.stylish_mycars.presentation.screen.vehiclepager.components.DashboardChartsSection
import com.segnities007.stylish_mycars.presentation.screen.vehiclepager.components.DashboardStatsGrid
import com.segnities007.stylish_mycars.presentation.screen.vehiclepager.components.UrgentAlertCard

@Composable
fun VehiclePagerScreen(
    viewModel: VehiclePagerViewModel,
    onNavigateToEdit: (Long?) -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToFuel: (Long) -> Unit,
    onNavigateToMaintenance: (Long) -> Unit,
    onNavigateToCost: (Long) -> Unit,
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
            }
        }
    }

    val pagerState = rememberPagerState(pageCount = { state.vehicles.size.coerceAtLeast(1) })

    LaunchedEffect(pagerState.currentPage) {
        viewModel.accept(VehiclePagerIntent.PageChanged(pagerState.currentPage))
    }

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.accept(VehiclePagerIntent.AddVehicle) },
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars),
                containerColor = MaterialTheme.colorScheme.onSurface,
                contentColor = MaterialTheme.colorScheme.surface,
            ) {
                Icon(Icons.Default.Add, contentDescription = "車両を追加")
            }
        },
    ) { _ ->
        Column(Modifier.fillMaxSize()) {
            if (state.vehicles.isEmpty() && !state.isLoading) {
                StylishEmptyState(
                    icon = Icons.Default.DirectionsCar,
                    title = "まだ車両が登録されていません",
                    description = "下の＋ボタンから最初の車を登録しましょう",
                    actionLabel = "車両を登録する",
                    onAction = { viewModel.accept(VehiclePagerIntent.AddVehicle) },
                )
            } else {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.weight(1f),
                ) { page ->
                    if (page < state.vehicles.size) {
                        val vehicle = state.vehicles[page]
                        VehiclePage(
                            vehicle = vehicle,
                            dashboard = state.dashboardFor(vehicle.id),
                            onIntent = viewModel::accept,
                        )
                    }
                }

                if (state.vehicles.size > 1) {
                    Row(
                        Modifier.fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        repeat(state.vehicles.size) { index ->
                            val isSelected = index == pagerState.currentPage
                            Box(
                                Modifier
                                    .padding(horizontal = 3.dp)
                                    .size(if (isSelected) 8.dp else 6.dp)
                                    .clip(CircleShape),
                            ) {
                                Surface(
                                    modifier = Modifier.fillMaxSize(),
                                    color = if (isSelected) MaterialTheme.colorScheme.onSurface
                                    else MaterialTheme.colorScheme.outlineVariant,
                                    shape = CircleShape,
                                ) {}
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun VehiclePage(
    vehicle: com.segnities007.stylish_mycars.domain.model.Vehicle,
    dashboard: VehicleDashboard,
    onIntent: (VehiclePagerIntent) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp),
    ) {
        // TopBarもスクロールコンテンツの一部（画面全体を有効活用）。
        // ステータスバー相当は StylishHeader 内の statusBarsPadding が処理する。
        item {
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
        }

        item {
            UrgentAlertCard(vehicle, dashboard)
            Spacer(Modifier.height(16.dp))
        }

        item {
            DashboardStatsGrid(
                dashboard = dashboard,
                onCostClick = { onIntent(VehiclePagerIntent.OpenCost(vehicle.id)) },
                onFuelClick = { onIntent(VehiclePagerIntent.OpenFuel(vehicle.id)) },
                showFuelEconomy = vehicle.category.usesFuel,
            )
            Spacer(Modifier.height(16.dp))
        }

        if (dashboard.fuelEconomyTrend.size >= 2 ||
            dashboard.costByCategory.size >= 2 ||
            dashboard.monthlyCostTrend.any { it.second > 0 }
        ) {
            item {
                DashboardChartsSection(dashboard)
                Spacer(Modifier.height(16.dp))
            }
        }

        item {
            StylishConnectedCardRow(
                items = buildList {
                    if (vehicle.category.usesFuel) {
                        add(
                            StylishConnectedCardItem(
                                title = "給油", supportingText = "記録・燃費",
                                onClick = { onIntent(VehiclePagerIntent.OpenFuel(vehicle.id)) },
                            ) { Icon(Icons.Default.LocalGasStation, null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                        )
                    }
                    add(
                        StylishConnectedCardItem(
                            title = "整備", supportingText = "記録・目安",
                            onClick = { onIntent(VehiclePagerIntent.OpenMaintenance(vehicle.id)) },
                        ) { Icon(Icons.Default.Build, null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                    )
                },
            )
            Spacer(Modifier.height(8.dp))
            StylishConnectedListItemColumn(
                items = listOf(
                    StylishConnectedListItem(
                        headline = "費用",
                        supportingText = "一覧・グラフ",
                        onClick = { onIntent(VehiclePagerIntent.OpenCost(vehicle.id)) },
                        trailingContent = {
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        },
                    ),
                ),
            )
        }

        item {
            Spacer(Modifier.height(20.dp))
            StylishSectionTitle("期限管理")
            VehicleDeadlineSection(
                vehicle = vehicle,
                onEdit = { onIntent(VehiclePagerIntent.EditVehicle(vehicle.id)) },
            )
        }

        item {
            Spacer(Modifier.height(20.dp))
            StylishSectionTitle("車両情報")
            VehicleInfoSection(vehicle)
            Spacer(Modifier.height(88.dp))
        }
    }
}
