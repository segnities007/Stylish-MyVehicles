package com.segnities007.stylish_myvehicles.presentation.screen.maintenance

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.segnities007.stylish_myvehicles.R
import com.segnities007.stylish_myvehicles.domain.model.MaintenanceCategory
import com.segnities007.stylish_myvehicles.domain.model.MaintenanceRecord
import com.segnities007.stylish_myvehicles.domain.model.Vehicle
import com.segnities007.stylish_myvehicles.domain.model.VehicleCategory
import com.segnities007.stylishui.components.atoms.StylishFab
import com.segnities007.stylishui.components.charts.BarChartData
import com.segnities007.stylishui.components.molecules.StylishConnectedCardGrid
import com.segnities007.stylishui.components.molecules.StylishConnectedListItemColumn
import com.segnities007.stylishui.components.molecules.StylishDeleteConfirmDialog
import com.segnities007.stylishui.components.molecules.StylishEmptyState
import com.segnities007.stylishui.components.models.StylishConnectedCardItem
import com.segnities007.stylishui.components.models.StylishConnectedListItem
import com.segnities007.stylishui.components.patterns.BarChartSection
import com.segnities007.stylish_myvehicles.presentation.screen.maintenance.MaintenanceRecordIntent
import com.segnities007.stylish_myvehicles.presentation.screen.maintenance.MaintenanceRecordViewModel
import com.segnities007.stylish_myvehicles.presentation.screen.maintenance.components.MaintenanceInputDialog
import com.segnities007.stylish_myvehicles.presentation.screen.records.Period
import com.segnities007.stylish_myvehicles.presentation.screen.records.PeriodMode
import com.segnities007.stylish_myvehicles.presentation.screen.records.RecordTopic
import com.segnities007.stylish_myvehicles.presentation.screen.records.RecordsLayout
import com.segnities007.stylish_myvehicles.presentation.screen.records.RecordsUiState
import com.segnities007.stylish_myvehicles.presentation.screen.records.RecordsViewModel
import com.segnities007.stylish_myvehicles.presentation.screen.records.subPeriods
import com.segnities007.stylish_myvehicles.presentation.theme.StylishMyVehiclesTheme
import java.time.LocalDate

/**
 * 整備の記録画面。期間ページャーで整備記録を閲覧し、FABから整備記録を追加できる。
 */
@Composable
fun MaintenanceRecordsScreen(
    viewModel: RecordsViewModel,
    dialogViewModel: MaintenanceRecordViewModel,
    onNavigateBack: () -> Unit,
    openAddDialog: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val dialogState by dialogViewModel.uiState.collectAsStateWithLifecycle()

    // ホームの「記録を追加」ダイアログから遷移してきた場合、すぐ入力Dialogを開く
    LaunchedEffect(openAddDialog) {
        if (openAddDialog) dialogViewModel.accept(MaintenanceRecordIntent.OpenAddDialog)
    }

    RecordsLayout(
        viewModel = viewModel,
        onNavigateBack = onNavigateBack,
        modifier = modifier,
        floatingActionButton = {
            StylishFab(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(R.string.record_maintenance),
                onClick = { dialogViewModel.accept(MaintenanceRecordIntent.OpenAddDialog) },
            )
        },
    ) { period ->
        MaintenancePeriodContent(
            period = period,
            state = state,
            onEditRecord = { dialogViewModel.accept(MaintenanceRecordIntent.EditRecord(it)) },
            onRequestDelete = { dialogViewModel.accept(MaintenanceRecordIntent.RequestDelete(it)) },
        )
    }

    if (dialogState.isDialogOpen) {
        MaintenanceInputDialog(
            state = dialogState,
            onIntent = dialogViewModel::accept,
        )
    }

    if (dialogState.deletingRecordId != null) {
        StylishDeleteConfirmDialog(
            title = stringResource(R.string.delete_maintenance_record),
            message = stringResource(R.string.delete_maintenance_record_message),
            confirmLabel = stringResource(R.string.delete),
            cancelLabel = stringResource(R.string.cancel),
            onConfirm = { dialogViewModel.accept(MaintenanceRecordIntent.ConfirmDelete) },
            onDismiss = { dialogViewModel.accept(MaintenanceRecordIntent.DismissDelete) },
        )
    }
}

/**
 * 整備の期間ページ中身。整備費用のグラフ、サマリーカード、記録一覧。
 */
internal fun LazyListScope.MaintenancePeriodContent(
    period: Period,
    state: RecordsUiState,
    onEditRecord: (Long) -> Unit,
    onRequestDelete: (Long) -> Unit,
) {
    val mode = state.periodMode
    val periodMaintenances =
        state.maintenanceRecords.filter { it.date in period.start..period.endInclusive }

    val periodMaintenanceCost = periodMaintenances.sumOf { it.cost }
    val year = period.start.year
    val maintenanceYearTotal =
        state.maintenanceRecords.filter { it.date.year == year }.sumOf { it.cost }
    val maintenanceTotal = state.maintenanceRecords.sumOf { it.cost }

    val subs = subPeriods(period, mode)
    val maintenanceCostTrend = subs.map { sp ->
        sp.label to state.maintenanceRecords.filter { it.date in sp.start..sp.endInclusive }
            .sumOf { it.cost }.toFloat()
    }

    item {
        val periodPrefix = when (mode) {
            PeriodMode.MONTHLY -> stringResource(R.string.this_month_prefix)
            PeriodMode.YEARLY -> stringResource(R.string.this_year_prefix)
            PeriodMode.WEEKLY -> stringResource(R.string.this_week_prefix)
            PeriodMode.ALL -> stringResource(R.string.all_period_prefix)
        }
        BarChartSection(
            title = stringResource(R.string.maintenance_cost_trend),
            data = maintenanceCostTrend.map { BarChartData(it.first, it.second) },
            contentDescriptionPrefix = stringResource(R.string.bar_chart),
            emptyLabel = stringResource(R.string.no_data),
        )
        Spacer(Modifier.height(12.dp))
        StylishConnectedCardGrid(
            columns = 3,
            items = listOf(
                StylishConnectedCardItem(
                    title = "${String.format("%,d", periodMaintenanceCost)}円",
                    supportingText = stringResource(R.string.period_maintenance_label, periodPrefix),
                ),
                StylishConnectedCardItem(
                    title = "${String.format("%,d", maintenanceYearTotal)}円",
                    supportingText = stringResource(R.string.yearly_maintenance_cost),
                ),
                StylishConnectedCardItem(
                    title = "${String.format("%,d", maintenanceTotal)}円",
                    supportingText = stringResource(R.string.total_maintenance_cost),
                ),
            ),
        )
        Spacer(Modifier.height(16.dp))
    }

    if (periodMaintenances.isEmpty()) {
        item {
            StylishEmptyState(
                icon = Icons.Default.Build,
                title = stringResource(R.string.no_records),
                description = stringResource(R.string.no_maintenance_records),
            )
        }
    } else {
        item {
            StylishConnectedListItemColumn(
                spacing = 4.dp,
                items = periodMaintenances.map { record ->
                    StylishConnectedListItem(
                        headline = record.title,
                        supportingLines = listOf(
                            record.date.toString(),
                            record.category.label,
                        ),
                        onClick = { onEditRecord(record.id) },
                        onLongClick = { onRequestDelete(record.id) },
                        trailingContent = {
                            if (record.cost > 0) {
                                Text(
                                    "${String.format("%,d", record.cost)}円",
                                    style = MaterialTheme.typography.titleMedium,
                                )
                            }
                        },
                    )
                },
            )
            // FABの裏にコンテンツが隠れないよう余白を確保する
            Spacer(Modifier.height(96.dp))
        }
    }
}

@Preview(name = "MaintenancePeriodContent", showBackground = true, widthDp = 393)
@Composable
private fun MaintenancePeriodContentPreview() {
    val vehicle = Vehicle(
        id = 1L,
        category = VehicleCategory.CAR,
        maker = "トヨタ",
        name = "カローラ",
    )
    val period = Period("2026年7月", LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 31))
    val state = RecordsUiState(
        vehicleId = 1L,
        topic = RecordTopic.MAINTENANCE,
        periodMode = PeriodMode.MONTHLY,
        vehicle = vehicle,
        periods = listOf(period),
        maintenanceRecords = listOf(
            MaintenanceRecord(
                id = 1L,
                vehicleId = 1L,
                date = LocalDate.of(2026, 7, 10),
                odometer = 49800,
                category = MaintenanceCategory.entries.first(),
                title = "エンジンオイル交換",
                cost = 8000,
                shopName = "トヨタディーラー",
            ),
        ),
        isLoading = false,
    )
    StylishMyVehiclesTheme {
        Surface(Modifier.padding(20.dp)) {
            LazyColumn {
                MaintenancePeriodContent(
                    period = period,
                    state = state,
                    onEditRecord = {},
                    onRequestDelete = {},
                )
            }
        }
    }
}
