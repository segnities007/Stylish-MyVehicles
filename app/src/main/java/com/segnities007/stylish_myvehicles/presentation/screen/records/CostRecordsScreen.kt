package com.segnities007.stylish_myvehicles.presentation.screen.records

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.segnities007.stylish_myvehicles.domain.model.CostCategory
import com.segnities007.stylish_myvehicles.domain.model.CostRecord
import com.segnities007.stylish_myvehicles.domain.model.Vehicle
import com.segnities007.stylish_myvehicles.domain.model.VehicleCategory
import com.segnities007.stylish_myvehicles.presentation.components.atoms.StylishFab
import com.segnities007.stylish_myvehicles.presentation.components.molecules.BarChartData
import com.segnities007.stylish_myvehicles.presentation.components.molecules.PieChartData
import com.segnities007.stylish_myvehicles.presentation.components.molecules.StylishConnectedCardGrid
import com.segnities007.stylish_myvehicles.presentation.components.molecules.StylishConnectedListItemColumn
import com.segnities007.stylish_myvehicles.presentation.components.molecules.StylishDeleteConfirmDialog
import com.segnities007.stylish_myvehicles.presentation.components.molecules.StylishEmptyState
import com.segnities007.stylish_myvehicles.presentation.components.molecules.costCategoryColor
import com.segnities007.stylish_myvehicles.presentation.components.molecules.models.StylishConnectedCardItem
import com.segnities007.stylish_myvehicles.presentation.components.molecules.models.StylishConnectedListItem
import com.segnities007.stylish_myvehicles.presentation.components.organisms.BarChartSection
import com.segnities007.stylish_myvehicles.presentation.components.organisms.PieChartSection
import com.segnities007.stylish_myvehicles.presentation.screen.cost.CostListIntent
import com.segnities007.stylish_myvehicles.presentation.screen.cost.CostListViewModel
import com.segnities007.stylish_myvehicles.presentation.screen.cost.components.CostInputDialog
import com.segnities007.stylish_myvehicles.presentation.theme.StylishMyVehiclesTheme
import java.time.LocalDate

/**
 * 費用の記録画面。期間ページャーで費用記録を閲覧し、FABから費用記録を追加できる。
 */
@Composable
fun CostRecordsScreen(
    viewModel: RecordsViewModel,
    dialogViewModel: CostListViewModel,
    onNavigateBack: () -> Unit,
    openAddDialog: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsState()
    val dialogState by dialogViewModel.uiState.collectAsState()

    // ホームの「記録を追加」ダイアログから遷移してきた場合、すぐ入力Dialogを開く
    LaunchedEffect(openAddDialog) {
        if (openAddDialog) dialogViewModel.accept(CostListIntent.OpenAddDialog)
    }

    RecordsLayout(
        viewModel = viewModel,
        onNavigateBack = onNavigateBack,
        modifier = modifier,
        floatingActionButton = {
            StylishFab(
                imageVector = Icons.Default.Add,
                contentDescription = "費用を記録",
                onClick = { dialogViewModel.accept(CostListIntent.OpenAddDialog) },
            )
        },
    ) { period ->
        CostPeriodContent(
            period = period,
            state = state,
            onEditRecord = { dialogViewModel.accept(CostListIntent.EditRecord(it)) },
            onRequestDelete = { dialogViewModel.accept(CostListIntent.RequestDelete(it)) },
        )
    }

    if (dialogState.isDialogOpen) {
        CostInputDialog(
            state = dialogState,
            onIntent = dialogViewModel::accept,
        )
    }

    if (dialogState.deletingRecordId != null) {
        StylishDeleteConfirmDialog(
            title = "費用記録を削除",
            message = "この費用記録を削除しますか？この操作は取り消せません。",
            onConfirm = { dialogViewModel.accept(CostListIntent.ConfirmDelete) },
            onDismiss = { dialogViewModel.accept(CostListIntent.DismissDelete) },
        )
    }
}

/**
 * 費用の期間ページ中身。費用カテゴリの円グラフ・費用推移のグラフ、サマリーカード、記録一覧。
 */
internal fun LazyListScope.CostPeriodContent(
    period: Period,
    state: RecordsUiState,
    onEditRecord: (Long) -> Unit,
    onRequestDelete: (Long) -> Unit,
) {
    val mode = state.periodMode
    val periodCosts = state.costRecords.filter { it.date in period.start..period.endInclusive }
    val periodPrefix = when (mode) {
        PeriodMode.MONTHLY -> "この月"
        PeriodMode.YEARLY -> "この年"
        PeriodMode.WEEKLY -> "この週"
        PeriodMode.ALL -> "全期間"
    }

    val periodCost = periodCosts.sumOf { it.amount }
    val year = period.start.year
    val costYearTotal = state.costRecords.filter { it.date.year == year }.sumOf { it.amount }
    val costTotal = state.costRecords.sumOf { it.amount }

    val subs = subPeriods(period, mode)
    val costTrend = subs.map { sp ->
        sp.label to state.costRecords.filter { it.date in sp.start..sp.endInclusive }
            .sumOf { it.amount }.toFloat()
    }
    val costByCategory = CostCategory.entries.mapNotNull { cat ->
        val total = periodCosts.filter { it.category == cat }.sumOf { it.amount }
        if (total > 0) cat to total else null
    }

    item {
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
        Spacer(Modifier.height(12.dp))
        StylishConnectedCardGrid(
            columns = 3,
            items = listOf(
                StylishConnectedCardItem(
                    title = "${String.format("%,d", periodCost)}円",
                    supportingText = "${periodPrefix}の費用",
                ),
                StylishConnectedCardItem(
                    title = "${String.format("%,d", costYearTotal)}円",
                    supportingText = "年間費用",
                ),
                StylishConnectedCardItem(
                    title = "${String.format("%,d", costTotal)}円",
                    supportingText = "総費用",
                ),
            ),
        )
        Spacer(Modifier.height(16.dp))
    }

    if (periodCosts.isEmpty()) {
        item {
            StylishEmptyState(
                icon = Icons.Default.AttachMoney,
                title = "記録なし",
                description = "費用の記録がありません",
            )
        }
    } else {
        item {
            StylishConnectedListItemColumn(
                spacing = 4.dp,
                items = periodCosts.map { cost ->
                    StylishConnectedListItem(
                        headline = "${cost.title} / ${String.format("%,d", cost.amount)}円",
                        supportingText = "${cost.date} / ${cost.category.label}",
                        onClick = { onEditRecord(cost.id) },
                        onLongClick = { onRequestDelete(cost.id) },
                    )
                },
            )
            // FABの裏にコンテンツが隠れないよう余白を確保する
            Spacer(Modifier.height(96.dp))
        }
    }
}

@Preview(name = "CostPeriodContent", showBackground = true, widthDp = 393)
@Composable
private fun CostPeriodContentPreview() {
    val vehicle = Vehicle(
        id = 1L,
        category = VehicleCategory.CAR,
        maker = "トヨタ",
        name = "カローラ",
    )
    val period = Period("2026年7月", LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 31))
    val state = RecordsUiState(
        vehicleId = 1L,
        topic = RecordTopic.COST,
        periodMode = PeriodMode.MONTHLY,
        vehicle = vehicle,
        periods = listOf(period),
        costRecords = listOf(
            CostRecord(
                id = 1L,
                vehicleId = 1L,
                date = LocalDate.of(2026, 7, 5),
                category = CostCategory.entries.first(),
                title = "駐車場代",
                amount = 12000,
            ),
            CostRecord(
                id = 2L,
                vehicleId = 1L,
                date = LocalDate.of(2026, 7, 20),
                category = CostCategory.entries.getOrNull(1) ?: CostCategory.entries.first(),
                title = "洗車",
                amount = 3000,
            ),
        ),
        isLoading = false,
    )
    StylishMyVehiclesTheme {
        Surface(Modifier.padding(20.dp)) {
            LazyColumn {
                CostPeriodContent(
                    period = period,
                    state = state,
                    onEditRecord = {},
                    onRequestDelete = {},
                )
            }
        }
    }
}
