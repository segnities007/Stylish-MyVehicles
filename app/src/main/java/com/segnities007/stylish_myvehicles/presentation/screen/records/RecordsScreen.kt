package com.segnities007.stylish_myvehicles.presentation.screen.records

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.segnities007.stylish_myvehicles.domain.model.CostCategory
import com.segnities007.stylish_myvehicles.domain.model.CostRecord
import com.segnities007.stylish_myvehicles.domain.model.FuelRecord
import com.segnities007.stylish_myvehicles.domain.model.MaintenanceRecord
import com.segnities007.stylish_myvehicles.domain.model.VehicleCategory
import com.segnities007.stylish_myvehicles.presentation.components.atoms.StylishIconButton
import com.segnities007.stylish_myvehicles.presentation.components.molecules.BarChartData
import com.segnities007.stylish_myvehicles.presentation.components.molecules.LineChartData
import com.segnities007.stylish_myvehicles.presentation.components.molecules.PieChartData
import com.segnities007.stylish_myvehicles.presentation.components.molecules.StylishConnectedCardGrid
import com.segnities007.stylish_myvehicles.presentation.components.molecules.StylishConnectedListItemColumn
import com.segnities007.stylish_myvehicles.presentation.components.molecules.StylishEmptyState
import com.segnities007.stylish_myvehicles.presentation.components.molecules.costCategoryColor
import com.segnities007.stylish_myvehicles.presentation.components.molecules.models.StylishConnectedCardItem
import com.segnities007.stylish_myvehicles.presentation.components.molecules.models.StylishConnectedListItem
import com.segnities007.stylish_myvehicles.presentation.components.organisms.BarChartSection
import com.segnities007.stylish_myvehicles.presentation.components.organisms.LineChartSection
import com.segnities007.stylish_myvehicles.presentation.components.organisms.PieChartSection
import com.segnities007.stylish_myvehicles.presentation.components.organisms.StylishHeader
import com.segnities007.stylish_myvehicles.presentation.components.organisms.StylishPageContent
import com.segnities007.stylish_myvehicles.presentation.components.organisms.StylishScaffold
import com.segnities007.stylish_myvehicles.presentation.screen.vehiclepager.components.PagerIndicator
import com.segnities007.stylish_myvehicles.presentation.theme.StylishMyVehiclesTheme
import java.time.LocalDate

@Composable
fun RecordsScreen(
    viewModel: RecordsViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToFuel: (Long, Long?) -> Unit,
    onNavigateToMaintenance: (Long, Long?) -> Unit,
    onNavigateToCost: (Long, Long?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is RecordsEffect.NavigateBack -> onNavigateBack()
                is RecordsEffect.OpenFuel -> onNavigateToFuel(effect.vehicleId, effect.recordId)
                is RecordsEffect.OpenMaintenance -> onNavigateToMaintenance(
                    effect.vehicleId,
                    effect.recordId
                )

                is RecordsEffect.OpenCost -> onNavigateToCost(effect.vehicleId, effect.recordId)
            }
        }
    }

    val pagerState = rememberPagerState(
        initialPage = state.periods.size,
        pageCount = { state.periods.size + 1 },
    )
    LaunchedEffect(pagerState.currentPage) {
        viewModel.accept(RecordsIntent.PageChanged(pagerState.currentPage))
    }

    val onChangePeriodMode: (PeriodMode) -> Unit = { mode ->
        PeriodPreference.setMode(context, mode)
        viewModel.accept(RecordsIntent.ChangePeriodMode(mode))
    }

    StylishScaffold(modifier = modifier) {
        Box(Modifier.fillMaxSize()) {
            when {
                state.isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                state.periods.isEmpty() -> StylishEmptyState(
                    icon = Icons.Default.AttachMoney,
                    title = "記録がありません",
                    description = "給油・整備・費用を記録しましょう",
                    modifier = Modifier.align(Alignment.Center),
                )

                else -> {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize(),
                    ) { page ->
                        if (page == 0) {
                            NoDataBoundaryPage(
                                onNavigateBack = { viewModel.accept(RecordsIntent.NavigateBack) },
                            )
                        } else {
                            val period = state.periods.getOrNull(page - 1) ?: return@HorizontalPager
                            PeriodPage(
                                topic = state.topic,
                                mode = state.periodMode,
                                period = period,
                                state = state,
                                onNavigateBack = { viewModel.accept(RecordsIntent.NavigateBack) },
                                onChangePeriodMode = onChangePeriodMode,
                                onAddFuel = { onNavigateToFuel(state.vehicleId, null) },
                                onEditFuel = { onNavigateToFuel(state.vehicleId, it) },
                                onAddMaintenance = { onNavigateToMaintenance(state.vehicleId, null) },
                                onEditMaintenance = { onNavigateToMaintenance(state.vehicleId, it) },
                                onAddCost = { onNavigateToCost(state.vehicleId, null) },
                                onEditCost = { onNavigateToCost(state.vehicleId, it) },
                            )
                        }
                    }
                    if (pagerState.pageCount > 1) {
                        PagerIndicator(
                            pagerState = pagerState,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 16.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NoDataBoundaryPage(
    onNavigateBack: () -> Unit,
) {
    StylishPageContent(
        header = {
            StylishHeader(
                title = {},
                navigation = {
                    StylishIconButton(
                        Icons.AutoMirrored.Filled.ArrowBack, "戻る",
                        onClick = onNavigateBack,
                    )
                },
            )
        },
    ) {
        item {
            StylishEmptyState(
                icon = Icons.Default.AttachMoney,
                title = "これより過去のデータはありません",
                description = "古い記録はありません",
            )
        }
    }
}

@Composable
private fun PeriodPage(
    topic: RecordTopic,
    mode: PeriodMode,
    period: Period,
    state: RecordsUiState,
    onNavigateBack: () -> Unit,
    onChangePeriodMode: (PeriodMode) -> Unit,
    onAddFuel: () -> Unit,
    onEditFuel: (Long) -> Unit,
    onAddMaintenance: () -> Unit,
    onEditMaintenance: (Long) -> Unit,
    onAddCost: () -> Unit,
    onEditCost: (Long) -> Unit,
) {
    val periodFuels = state.fuelRecords.filter { it.date in period.start..period.endInclusive }
    val periodMaintenances =
        state.maintenanceRecords.filter { it.date in period.start..period.endInclusive }
    val periodCosts = state.costRecords.filter { it.date in period.start..period.endInclusive }

    val hasAny = when (topic) {
        RecordTopic.FUEL -> periodFuels.isNotEmpty()
        RecordTopic.MAINTENANCE -> periodMaintenances.isNotEmpty()
        RecordTopic.COST -> periodCosts.isNotEmpty()
    }

    val periodPrefix = when (mode) {
        PeriodMode.MONTHLY -> "この月"
        PeriodMode.YEARLY -> "この年"
        PeriodMode.WEEKLY -> "この週"
    }

    val periodFuelCost = periodFuels.sumOf { it.amount }
    val periodMaintenanceCost = periodMaintenances.sumOf { it.cost }
    val periodCost = periodCosts.sumOf { it.amount }
    val periodFuelEconomy = periodFuels.mapNotNull { it.fuelEconomy }
        .takeIf { it.isNotEmpty() }
        ?.average()

    val year = period.start.year
    val fuelYearTotal = state.fuelRecords.filter { it.date.year == year }.sumOf { it.amount }
    val maintenanceYearTotal =
        state.maintenanceRecords.filter { it.date.year == year }.sumOf { it.cost }
    val costYearTotal = state.costRecords.filter { it.date.year == year }.sumOf { it.amount }
    val fuelTotal = state.fuelRecords.sumOf { it.amount }
    val maintenanceTotal = state.maintenanceRecords.sumOf { it.cost }
    val costTotal = state.costRecords.sumOf { it.amount }

    val subs = subPeriods(period, mode)
    val fuelEconomyTrend = subs.map { sp ->
        sp.label to (state.fuelRecords.filter { it.date in sp.start..sp.endInclusive }
            .mapNotNull { it.fuelEconomy }.takeIf { it.isNotEmpty() }?.average()?.toFloat() ?: 0f)
    }
    val fuelCostTrend = subs.map { sp ->
        sp.label to state.fuelRecords.filter { it.date in sp.start..sp.endInclusive }
            .sumOf { it.amount }.toFloat()
    }
    val maintenanceCostTrend = subs.map { sp ->
        sp.label to state.maintenanceRecords.filter { it.date in sp.start..sp.endInclusive }
            .sumOf { it.cost }.toFloat()
    }
    val costTrend = subs.map { sp ->
        sp.label to state.costRecords.filter { it.date in sp.start..sp.endInclusive }
            .sumOf { it.amount }.toFloat()
    }
    val costByCategory = CostCategory.entries.mapNotNull { cat ->
        val total = periodCosts.filter { it.category == cat }.sumOf { it.amount }
        if (total > 0) cat to total else null
    }

    var showModeMenu by remember { mutableStateOf(false) }

    StylishPageContent(
        header = {
            StylishHeader(
                title = {
                    Text(
                        period.label,
                        style = MaterialTheme.typography.titleLarge,
                    )
                },
                navigation = {
                    StylishIconButton(
                        Icons.AutoMirrored.Filled.ArrowBack, "戻る",
                        onClick = onNavigateBack,
                    )
                },
                actions = {
                    Box {
                        StylishIconButton(
                            Icons.Default.Settings, "表示設定",
                            onClick = { showModeMenu = true },
                        )
                        DropdownMenu(
                            expanded = showModeMenu,
                            onDismissRequest = { showModeMenu = false },
                        ) {
                            PeriodMode.entries.forEach { m ->
                                DropdownMenuItem(
                                    text = { Text(m.label) },
                                    onClick = {
                                        showModeMenu = false
                                        onChangePeriodMode(m)
                                    },
                                )
                            }
                        }
                    }
                },
            )
        },
    ) {
        item {
            when (topic) {
                RecordTopic.FUEL -> {
                    LineChartSection(
                        title = "燃費推移 (km/L)",
                        data = fuelEconomyTrend.map { LineChartData(it.first, it.second) },
                    )
                    Spacer(Modifier.height(8.dp))
                    BarChartSection(
                        title = "給油費用の推移",
                        data = fuelCostTrend.map { BarChartData(it.first, it.second) },
                    )
                }

                RecordTopic.MAINTENANCE -> {
                    BarChartSection(
                        title = "整備費用の推移",
                        data = maintenanceCostTrend.map { BarChartData(it.first, it.second) },
                    )
                }

                RecordTopic.COST -> {
                    PieChartSection(
                        title = "費用カテゴリ",
                        data = costByCategory.map { (cat, total) ->
                            PieChartData(cat.label, total.toFloat(), costCategoryColor(cat.ordinal))
                        },
                    )
                    Spacer(Modifier.height(8.dp))
                    BarChartSection(
                        title = "費用の推移",
                        data = costTrend.map { BarChartData(it.first, it.second) },
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            StylishConnectedCardGrid(
                columns = 3,
                items = when (topic) {
                    RecordTopic.FUEL -> buildList {
                        add(
                            StylishConnectedCardItem(
                                title = "${String.format("%,d", periodFuelCost)}円",
                                supportingText = "${periodPrefix}の給油",
                            )
                        )
                        add(
                            StylishConnectedCardItem(
                                title = "${String.format("%,d", fuelYearTotal)}円",
                                supportingText = "年間給油費用",
                            )
                        )
                        add(
                            StylishConnectedCardItem(
                                title = "${String.format("%,d", fuelTotal)}円",
                                supportingText = "総給油費用",
                            )
                        )
                        if (state.vehicle?.category?.usesFuel == true) {
                            add(
                                StylishConnectedCardItem(
                                    title = periodFuelEconomy?.let { "%.1f km/L".format(it) }
                                        ?: "--",
                                    supportingText = "平均燃費",
                                )
                            )
                        }
                    }

                    RecordTopic.MAINTENANCE -> buildList {
                        add(
                            StylishConnectedCardItem(
                                title = "${String.format("%,d", periodMaintenanceCost)}円",
                                supportingText = "${periodPrefix}の整備",
                            )
                        )
                        add(
                            StylishConnectedCardItem(
                                title = "${String.format("%,d", maintenanceYearTotal)}円",
                                supportingText = "年間整備費用",
                            )
                        )
                        add(
                            StylishConnectedCardItem(
                                title = "${String.format("%,d", maintenanceTotal)}円",
                                supportingText = "総整備費用",
                            )
                        )
                    }

                    RecordTopic.COST -> buildList {
                        add(
                            StylishConnectedCardItem(
                                title = "${String.format("%,d", periodCost)}円",
                                supportingText = "${periodPrefix}の費用",
                            )
                        )
                        add(
                            StylishConnectedCardItem(
                                title = "${String.format("%,d", costYearTotal)}円",
                                supportingText = "年間費用",
                            )
                        )
                        add(
                            StylishConnectedCardItem(
                                title = "${String.format("%,d", costTotal)}円",
                                supportingText = "総費用",
                            )
                        )
                    }
                },
            )
            Spacer(Modifier.height(16.dp))
        }

        if (!hasAny) {
            item {
                StylishEmptyState(
                    icon = when (topic) {
                        RecordTopic.FUEL -> Icons.Default.LocalGasStation
                        RecordTopic.MAINTENANCE -> Icons.Default.Build
                        RecordTopic.COST -> Icons.Default.AttachMoney
                    },
                    title = "記録なし",
                    description = "${topic.label}の記録がありません",
                )
            }
        }

        when (topic) {
            RecordTopic.FUEL -> if (periodFuels.isNotEmpty()) {
                item {
                    StylishConnectedListItemColumn(
                        spacing = 4.dp,
                        items = periodFuels.map { fuel ->
                            StylishConnectedListItem(
                                headline = "${fuel.volume}L / ${String.format("%,d", fuel.amount)}円",
                                supportingText = buildList {
                                    add(fuel.date.toString())
                                    fuel.fuelEconomy?.let { add("%.1f km/L".format(it)) }
                                }.joinToString(" / "),
                                onClick = { onEditFuel(fuel.id) },
                            )
                        },
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }

            RecordTopic.MAINTENANCE -> if (periodMaintenances.isNotEmpty()) {
                item {
                    StylishConnectedListItemColumn(
                        spacing = 4.dp,
                        items = periodMaintenances.map { record ->
                            StylishConnectedListItem(
                                headline = record.title,
                                supportingText = buildList {
                                    add(record.date.toString())
                                    if (record.cost > 0) add("${String.format("%,d", record.cost)}円")
                                    add(record.category.label)
                                }.joinToString(" / "),
                                onClick = { onEditMaintenance(record.id) },
                            )
                        },
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }

            RecordTopic.COST -> if (periodCosts.isNotEmpty()) {
                item {
                    StylishConnectedListItemColumn(
                        spacing = 4.dp,
                        items = periodCosts.map { cost ->
                            StylishConnectedListItem(
                                headline = "${cost.title} / ${String.format("%,d", cost.amount)}円",
                                supportingText = "${cost.date} / ${cost.category.label}",
                                onClick = { onEditCost(cost.id) },
                            )
                        },
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }
        }

        item {
            Spacer(Modifier.height(16.dp))
            when (topic) {
                RecordTopic.FUEL -> AddButton(
                    icon = Icons.Default.LocalGasStation,
                    label = "給油を記録",
                    onClick = onAddFuel,
                )

                RecordTopic.MAINTENANCE -> AddButton(
                    icon = Icons.Default.Build,
                    label = "整備を記録",
                    onClick = onAddMaintenance,
                )

                RecordTopic.COST -> AddButton(
                    icon = Icons.Default.AttachMoney,
                    label = "費用を記録",
                    onClick = onAddCost,
                )
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun AddButton(icon: ImageVector, label: String, onClick: () -> Unit) {
    FilledTonalButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = MaterialTheme.shapes.medium,
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Text(label)
    }
}

@Preview(name = "PeriodPage", showBackground = true, widthDp = 393)
@Composable
private fun PeriodPagePreview() {
    val vehicle = com.segnities007.stylish_myvehicles.domain.model.Vehicle(
        id = 1L,
        category = VehicleCategory.CAR,
        maker = "トヨタ",
        name = "カローラ",
    )
    val state = RecordsUiState(
        vehicleId = 1L,
        topic = RecordTopic.COST,
        periodMode = PeriodMode.MONTHLY,
        vehicle = vehicle,
        periods = listOf(
            Period("2026年7月", LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 31))
        ),
        isLoading = false,
    )
    StylishMyVehiclesTheme {
        Surface(Modifier.padding(20.dp)) {
            PeriodPage(
                topic = RecordTopic.COST,
                mode = PeriodMode.MONTHLY,
                period = state.periods.first(),
                state = state,
                onNavigateBack = {},
                onChangePeriodMode = {},
                onAddFuel = {},
                onEditFuel = {},
                onAddMaintenance = {},
                onEditMaintenance = {},
                onAddCost = {},
                onEditCost = {},
            )
        }
    }
}
