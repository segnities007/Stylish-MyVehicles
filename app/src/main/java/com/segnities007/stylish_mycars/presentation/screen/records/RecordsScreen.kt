package com.segnities007.stylish_mycars.presentation.screen.records

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.segnities007.stylish_mycars.domain.model.CostCategory
import com.segnities007.stylish_mycars.domain.model.CostRecord
import com.segnities007.stylish_mycars.domain.model.FuelRecord
import com.segnities007.stylish_mycars.domain.model.MaintenanceRecord
import com.segnities007.stylish_mycars.domain.model.VehicleCategory
import com.segnities007.stylish_mycars.presentation.components.atoms.StylishIconButton
import com.segnities007.stylish_mycars.presentation.components.molecules.BarChartData
import com.segnities007.stylish_mycars.presentation.components.molecules.PieChartData
import com.segnities007.stylish_mycars.presentation.components.molecules.StylishConnectedCardGrid
import com.segnities007.stylish_mycars.presentation.components.molecules.StylishConnectedListItemColumn
import com.segnities007.stylish_mycars.presentation.components.molecules.StylishEmptyState
import com.segnities007.stylish_mycars.presentation.components.molecules.costCategoryColor
import com.segnities007.stylish_mycars.presentation.components.molecules.models.StylishConnectedCardItem
import com.segnities007.stylish_mycars.presentation.components.molecules.models.StylishConnectedListItem
import com.segnities007.stylish_mycars.presentation.components.organisms.BarChartSection
import com.segnities007.stylish_mycars.presentation.components.organisms.PieChartSection
import com.segnities007.stylish_mycars.presentation.components.organisms.StylishHeader
import com.segnities007.stylish_mycars.presentation.components.organisms.StylishPageContent
import com.segnities007.stylish_mycars.presentation.components.organisms.StylishScaffold
import com.segnities007.stylish_mycars.presentation.components.organisms.StylishSectionTitle
import com.segnities007.stylish_mycars.presentation.theme.StylishMyCarsTheme
import kotlinx.coroutines.launch
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
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

    val pagerState = rememberPagerState(pageCount = { state.months.size.coerceAtLeast(1) })

    LaunchedEffect(pagerState.currentPage) {
        viewModel.accept(RecordsIntent.PageChanged(pagerState.currentPage))
    }

    StylishScaffold(modifier = modifier) {
        Box(Modifier.fillMaxSize()) {
            when {
                state.isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                state.months.isEmpty() -> StylishEmptyState(
                    icon = Icons.Default.AttachMoney,
                    title = "記録がありません",
                    description = "給油・整備・費用を記録しましょう",
                    modifier = Modifier.align(Alignment.Center),
                )
                else -> HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
            ) { page ->
                val month = state.months.getOrNull(page) ?: return@HorizontalPager
                MonthPage(
                    month = month,
                    state = state,
                    fuels = state.fuelByMonth[month].orEmpty(),
                    maintenances = state.maintenanceByMonth[month].orEmpty(),
                    costs = state.costByMonth[month].orEmpty(),
                    onNavigateBack = { viewModel.accept(RecordsIntent.NavigateBack) },
                    onAddFuel = { onNavigateToFuel(state.vehicleId, null) },
                    onEditFuel = { onNavigateToFuel(state.vehicleId, it) },
                    onAddMaintenance = { onNavigateToMaintenance(state.vehicleId, null) },
                    onEditMaintenance = { onNavigateToMaintenance(state.vehicleId, it) },
                    onAddCost = { onNavigateToCost(state.vehicleId, null) },
                    onEditCost = { onNavigateToCost(state.vehicleId, it) },
                )
        }
    }
}
}
}

@Composable
private fun MonthPage(
    month: YearMonth,
    state: RecordsUiState,
    fuels: List<FuelRecord>,
    maintenances: List<MaintenanceRecord>,
    costs: List<CostRecord>,
    onNavigateBack: () -> Unit,
    onAddFuel: () -> Unit,
    onEditFuel: (Long) -> Unit,
    onAddMaintenance: () -> Unit,
    onEditMaintenance: (Long) -> Unit,
    onAddCost: () -> Unit,
    onEditCost: (Long) -> Unit,
) {
    val monthTotal = fuels.sumOf { it.amount } +
            maintenances.sumOf { it.cost } +
            costs.sumOf { it.amount }
    val hasAny = fuels.isNotEmpty() || maintenances.isNotEmpty() || costs.isNotEmpty()

    StylishPageContent(
        header = {
            StylishHeader(
                title = {
                    Text(
                        month.format(DateTimeFormatter.ofPattern("yyyy年M月")),
                        style = MaterialTheme.typography.titleLarge,
                    )
                },
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
            StylishConnectedCardGrid(
                columns = 3,
                items = buildList {
                    add(
                        StylishConnectedCardItem(
                            title = "${String.format("%,d", state.monthlyCost)}円",
                            supportingText = "今月の費用",
                        )
                    )
                    add(
                        StylishConnectedCardItem(
                            title = "${String.format("%,d", state.yearlyCost)}円",
                            supportingText = "年間費用",
                        )
                    )
                    add(
                        StylishConnectedCardItem(
                            title = "${String.format("%,d", state.totalCost)}円",
                            supportingText = "総費用",
                        )
                    )
                    add(
                        StylishConnectedCardItem(
                            title = state.averageMonthlyCost?.let {
                            "${
                                String.format(
                                    "%,d",
                                    it.toInt()
                                )
                            }円"
                        } ?: "--",
                        supportingText = "月平均",
                    ))
                    state.vehicle?.let { v ->
                        if (v.category.usesFuel) {
                            add(
                                StylishConnectedCardItem(
                                title = state.averageFuelEconomy?.let { "%.1f km/L".format(it) }
                                    ?: "--",
                                supportingText = "平均燃費",
                            ))
                        }
                    }
                },
            )
            Spacer(Modifier.height(12.dp))
            PieChartSection(
                title = "費用カテゴリ",
                data = state.costByCategory.map { (cat, total) ->
                    PieChartData(cat.label, total.toFloat(), costCategoryColor(cat.ordinal))
                },
            )
            Spacer(Modifier.height(8.dp))
            BarChartSection(
                title = "月次費用",
                data = state.monthlyCostTrend.map { BarChartData(it.first, it.second) },
            )
            Spacer(Modifier.height(16.dp))
        }
        item {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    "月間合計",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    "${String.format("%,d", monthTotal)}円",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                )
            }
            Spacer(Modifier.height(8.dp))
        }

        if (!hasAny) {
            item {
                StylishEmptyState(
                    icon = Icons.Default.AttachMoney,
                    title = "${month.monthValue}月の記録はありません",
                    description = "給油・整備・費用を記録しましょう",
                )
            }
        }

        if (fuels.isNotEmpty()) {
            item {
                StylishSectionTitle("給油")
                Spacer(Modifier.height(8.dp))
            }
            item {
                StylishConnectedListItemColumn(
                    spacing = 4.dp,
                    items = fuels.map { fuel ->
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

        if (maintenances.isNotEmpty()) {
            item {
                StylishSectionTitle("整備")
                Spacer(Modifier.height(8.dp))
            }
            item {
                StylishConnectedListItemColumn(
                    spacing = 4.dp,
                    items = maintenances.map { record ->
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

        if (costs.isNotEmpty()) {
            item {
                StylishSectionTitle("費用")
                Spacer(Modifier.height(8.dp))
            }
            item {
                StylishConnectedListItemColumn(
                    spacing = 4.dp,
                    items = costs.map { cost ->
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

        item {
            Spacer(Modifier.height(16.dp))
            RecordAddButtons(
                onAddFuel = onAddFuel,
                onAddMaintenance = onAddMaintenance,
                onAddCost = onAddCost,
            )
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun RecordAddButtons(
    onAddFuel: () -> Unit,
    onAddMaintenance: () -> Unit,
    onAddCost: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        AddButton(icon = Icons.Default.LocalGasStation, label = "給油を記録", onClick = onAddFuel)
        AddButton(icon = Icons.Default.Build, label = "整備を記録", onClick = onAddMaintenance)
        AddButton(icon = Icons.Default.AttachMoney, label = "費用を記録", onClick = onAddCost)
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

@Preview(name = "MonthPage", showBackground = true, widthDp = 393)
@Composable
private fun MonthPagePreview() {
    val vehicle = com.segnities007.stylish_mycars.domain.model.Vehicle(
        id = 1L,
        category = VehicleCategory.CAR,
        maker = "トヨタ",
        name = "カローラ",
    )
    val state = RecordsUiState(
        vehicleId = 1L,
        vehicle = vehicle,
        months = listOf(YearMonth.of(2026, 7)),
        isLoading = false,
        monthlyCost = 25000,
        yearlyCost = 300000,
        totalCost = 1500000,
        averageMonthlyCost = 25000.0,
        averageFuelEconomy = 15.5,
        totalDistance = 45000,
        costByCategory = listOf(
            CostCategory.FUEL to 80000,
            CostCategory.MAINTENANCE to 30000,
        ),
        monthlyCostTrend = listOf(
            "1月" to 25000f,
            "2月" to 28000f,
            "3月" to 22000f,
        ),
    )
    StylishMyCarsTheme {
        Surface(Modifier.padding(20.dp)) {
            MonthPage(
                month = YearMonth.of(2026, 7),
                state = state,
                fuels = emptyList(),
                maintenances = emptyList(),
                costs = emptyList(),
                onNavigateBack = {},
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

@Preview(name = "RecordAddButtons", showBackground = true, widthDp = 393)
@Composable
private fun RecordAddButtonsPreview() {
    StylishMyCarsTheme {
        Surface(Modifier.padding(20.dp)) {
            RecordAddButtons(
                onAddFuel = {},
                onAddMaintenance = {},
                onAddCost = {},
            )
        }
    }
}

@Preview(name = "AddButton", showBackground = true, widthDp = 393)
@Composable
private fun AddButtonPreview() {
    StylishMyCarsTheme {
        Surface(Modifier.padding(20.dp)) {
            AddButton(
                icon = Icons.Default.LocalGasStation,
                label = "給油を記録",
                onClick = {},
            )
        }
    }
}
