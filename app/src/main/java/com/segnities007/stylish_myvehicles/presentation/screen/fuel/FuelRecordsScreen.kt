package com.segnities007.stylish_myvehicles.presentation.screen.fuel

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.segnities007.stylish_myvehicles.R
import com.segnities007.stylish_myvehicles.data.ocr.ReceiptScanner
import com.segnities007.stylish_myvehicles.domain.model.FuelRecord
import com.segnities007.stylish_myvehicles.domain.model.Vehicle
import com.segnities007.stylish_myvehicles.domain.model.VehicleCategory
import com.segnities007.stylishui.components.atoms.StylishFab
import com.segnities007.stylishui.components.charts.BarChartData
import com.segnities007.stylishui.components.charts.LineChartData
import com.segnities007.stylishui.components.atoms.StylishDialogSurface
import com.segnities007.stylishui.components.molecules.StylishConnectedCardGrid
import com.segnities007.stylishui.components.molecules.StylishConnectedCardColumn
import com.segnities007.stylishui.components.molecules.StylishEmptyState
import com.segnities007.stylishui.components.organisms.StylishDeleteConfirmDialog
import com.segnities007.stylishui.components.models.StylishConnectedCardItem
import com.segnities007.stylishui.components.patterns.BarChartSection
import com.segnities007.stylishui.components.patterns.LineChartSection
import com.segnities007.stylish_myvehicles.presentation.screen.fuel.FuelRecordIntent
import com.segnities007.stylish_myvehicles.presentation.screen.fuel.FuelRecordViewModel
import com.segnities007.stylish_myvehicles.presentation.screen.fuel.components.FuelInputDialog
import com.segnities007.stylish_myvehicles.presentation.screen.records.Period
import com.segnities007.stylish_myvehicles.presentation.screen.records.PeriodMode
import com.segnities007.stylish_myvehicles.presentation.screen.records.RecordTopic
import com.segnities007.stylish_myvehicles.presentation.screen.records.RecordsLayout
import com.segnities007.stylish_myvehicles.presentation.screen.records.RecordsUiState
import com.segnities007.stylish_myvehicles.presentation.screen.records.RecordsViewModel
import com.segnities007.stylish_myvehicles.presentation.screen.records.subPeriods
import com.segnities007.stylish_myvehicles.presentation.theme.StylishMyVehiclesTheme
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDate

/**
 * 給油の記録画面。期間ページャーで給油記録を閲覧し、FABから給油記録を追加できる。
 */
@Composable
fun FuelRecordsScreen(
    viewModel: RecordsViewModel,
    dialogViewModel: FuelRecordViewModel,
    onNavigateBack: () -> Unit,
    openAddDialog: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val dialogState by dialogViewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var receiptImageUri by remember { mutableStateOf<Uri?>(null) }
    var showReceiptSourceDialog by remember { mutableStateOf(false) }

    // ホームの「記録を追加」ダイアログから遷移してきた場合、すぐ入力Dialogを開く
    LaunchedEffect(openAddDialog) {
        if (openAddDialog) dialogViewModel.accept(FuelRecordIntent.OpenAddDialog)
    }

    // 取得した画像（カメラ撮影 or ギャラリー選択）をレシート読み取りにかける共通処理
    fun scanReceipt(uri: Uri) {
        dialogViewModel.accept(FuelRecordIntent.ScanningChanged(true))
        scope.launch {
            val result = runCatching { ReceiptScanner.scan(context, uri) }
            result.onSuccess { data ->
                dialogViewModel.accept(
                    FuelRecordIntent.ReceiptScanned(
                        volume = data.volume,
                        amount = data.amount,
                        odometer = data.odometer,
                    ),
                )
            }
            dialogViewModel.accept(FuelRecordIntent.ScanningChanged(false))
        }
    }

    // カメラを起動して撮影し、その画像をレシート読み取りに使用する
    val receiptScanLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
    ) { success ->
        val uri = receiptImageUri
        if (success && uri != null) scanReceipt(uri)
    }

    // ギャラリーから画像を選択し、その画像をレシート読み取りに使用する
    val receiptPickLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri ->
        if (uri != null) scanReceipt(uri)
    }

    RecordsLayout(
        viewModel = viewModel,
        onNavigateBack = onNavigateBack,
        modifier = modifier,
        floatingActionButton = {
            StylishFab(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(R.string.record_fuel),
                onClick = { dialogViewModel.accept(FuelRecordIntent.OpenAddDialog) },
            )
        },
    ) { period ->
        FuelPeriodContent(
            period = period,
            state = state,
            onEditRecord = { dialogViewModel.accept(FuelRecordIntent.EditRecord(it)) },
            onRequestDelete = { dialogViewModel.accept(FuelRecordIntent.RequestDelete(it)) },
        )
    }

    if (dialogState.isDialogOpen) {
        FuelInputDialog(
            state = dialogState,
            onIntent = dialogViewModel::accept,
            onScanReceipt = { showReceiptSourceDialog = true },
        )
    }

    // レシート画像の取得元（カメラ / 写真 / キャンセル）を選択するダイアログ
    if (showReceiptSourceDialog) {
        StylishDialogSurface(onDismiss = { showReceiptSourceDialog = false }) {
            Column(Modifier.padding(24.dp)) {
                Text(stringResource(R.string.read_receipt), style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(16.dp))
                StylishConnectedCardGrid(
                    columns = 2,
                    spacing = 4.dp,
                    items = listOf(
                        StylishConnectedCardItem(
                            title = stringResource(R.string.camera),
                            onClick = {
                                showReceiptSourceDialog = false
                                val uri = createReceiptImageUri(context)
                                receiptImageUri = uri
                                receiptScanLauncher.launch(uri)
                            },
                            trailingContent = {
                                Icon(
                                    Icons.Default.CameraAlt,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            },
                        ),
                        StylishConnectedCardItem(
                            title = stringResource(R.string.photo),
                            onClick = {
                                showReceiptSourceDialog = false
                                receiptPickLauncher.launch("image/*")
                            },
                            trailingContent = {
                                Icon(
                                    Icons.Default.PhotoLibrary,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            },
                        ),
                        StylishConnectedCardItem(
                            title = stringResource(R.string.cancel),
                            onClick = { showReceiptSourceDialog = false },
                        ),
                    ),
                )
            }
        }
    }

    if (dialogState.deletingRecordId != null) {
        StylishDeleteConfirmDialog(
            title = stringResource(R.string.delete_fuel_record),
            message = stringResource(R.string.delete_fuel_record_message),
            confirmLabel = stringResource(R.string.delete),
            cancelLabel = stringResource(R.string.cancel),
            onConfirm = { dialogViewModel.accept(FuelRecordIntent.ConfirmDelete) },
            onDismiss = { dialogViewModel.accept(FuelRecordIntent.DismissDelete) },
        )
    }
}

/**
 * カメラ撮影用の一時画像UriをFileProvider経由で作成する。
 */
private fun createReceiptImageUri(context: Context): Uri {
    val dir = File(context.cacheDir, "receipt_images").apply { mkdirs() }
    val file = File.createTempFile("receipt_", ".jpg", dir)
    return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
}

/**
 * 給油の期間ページ中身。燃費推移・給油費用のグラフ、サマリーカード、記録一覧。
 */
internal fun LazyListScope.FuelPeriodContent(
    period: Period,
    state: RecordsUiState,
    onEditRecord: (Long) -> Unit,
    onRequestDelete: (Long) -> Unit,
) {
    val mode = state.periodMode
    val periodFuels = state.fuelRecords.filter { it.date in period.start..period.endInclusive }

    val periodFuelCost = periodFuels.sumOf { it.amount }
    val periodFuelEconomy = periodFuels.mapNotNull { it.fuelEconomy }
        .takeIf { it.isNotEmpty() }
        ?.average()
    val year = period.start.year
    val fuelYearTotal = state.fuelRecords.filter { it.date.year == year }.sumOf { it.amount }
    val fuelTotal = state.fuelRecords.sumOf { it.amount }

    val subs = subPeriods(period, mode)
    val fuelEconomyTrend = subs.map { sp ->
        sp.label to (state.fuelRecords.filter { it.date in sp.start..sp.endInclusive }
            .mapNotNull { it.fuelEconomy }.takeIf { it.isNotEmpty() }?.average()?.toFloat() ?: 0f)
    }
    val fuelCostTrend = subs.map { sp ->
        sp.label to state.fuelRecords.filter { it.date in sp.start..sp.endInclusive }
            .sumOf { it.amount }.toFloat()
    }

    item {
        val periodPrefix = when (mode) {
            PeriodMode.MONTHLY -> stringResource(R.string.this_month_prefix)
            PeriodMode.YEARLY -> stringResource(R.string.this_year_prefix)
            PeriodMode.WEEKLY -> stringResource(R.string.this_week_prefix)
            PeriodMode.ALL -> stringResource(R.string.all_period_prefix)
        }
        LineChartSection(
            title = stringResource(R.string.fuel_economy_trend),
            data = fuelEconomyTrend.map { LineChartData(it.first, it.second) },
            contentDescriptionPrefix = stringResource(R.string.line_chart),
            emptyLabel = stringResource(R.string.no_data),
        )
        Spacer(Modifier.height(8.dp))
        BarChartSection(
            title = stringResource(R.string.fuel_cost_trend),
            data = fuelCostTrend.map { BarChartData(it.first, it.second) },
            contentDescriptionPrefix = stringResource(R.string.bar_chart),
            emptyLabel = stringResource(R.string.no_data),
        )
        Spacer(Modifier.height(12.dp))
        StylishConnectedCardGrid(
            columns = 3,
            items = buildList {
                add(
                    StylishConnectedCardItem(
                        title = "${String.format("%,d", periodFuelCost)}円",
                        supportingText = stringResource(R.string.period_fuel_label, periodPrefix),
                    )
                )
                add(
                    StylishConnectedCardItem(
                        title = "${String.format("%,d", fuelYearTotal)}円",
                        supportingText = stringResource(R.string.yearly_fuel_cost),
                    )
                )
                add(
                    StylishConnectedCardItem(
                        title = "${String.format("%,d", fuelTotal)}円",
                        supportingText = stringResource(R.string.total_fuel_cost),
                    )
                )
                if (state.vehicle?.category?.usesFuel == true) {
                    add(
                        StylishConnectedCardItem(
                            title = periodFuelEconomy?.let { "%.1f km/L".format(it) } ?: "--",
                            supportingText = stringResource(R.string.average_fuel_economy_label),
                        )
                    )
                }
            },
        )
        Spacer(Modifier.height(16.dp))
    }

    if (periodFuels.isEmpty()) {
        item {
            StylishEmptyState(
                icon = Icons.Default.LocalGasStation,
                title = stringResource(R.string.no_records),
                description = stringResource(R.string.no_fuel_records),
            )
        }
    } else {
        item {
            StylishConnectedCardColumn(
                spacing = 4.dp,
                items = periodFuels.map { fuel ->
                    StylishConnectedCardItem(
                        title = "${fuel.volume}L",
                        supportingText =
                            listOfNotNull(
                                fuel.date.toString(),
                                fuel.fuelEconomy?.let { "%.1f km/L".format(it) },
                            ).joinToString("\n"),
                        onClick = { onEditRecord(fuel.id) },
                        onLongClick = { onRequestDelete(fuel.id) },
                        trailingContent = {
                            Text(
                                "${String.format("%,d", fuel.amount)}円",
                                style = MaterialTheme.typography.titleMedium,
                            )
                        },
                    )
                },
            )
            // FABの裏にコンテンツが隠れないよう余白を確保する
            Spacer(Modifier.height(96.dp))
        }
    }
}

@Preview(name = "FuelPeriodContent", showBackground = true, widthDp = 393)
@Composable
private fun FuelPeriodContentPreview() {
    val vehicle = Vehicle(
        id = 1L,
        category = VehicleCategory.CAR,
        maker = "トヨタ",
        name = "カローラ",
    )
    val period = Period("2026年7月", LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 31))
    val state = RecordsUiState(
        vehicleId = 1L,
        topic = RecordTopic.FUEL,
        periodMode = PeriodMode.MONTHLY,
        vehicle = vehicle,
        periods = listOf(period),
        fuelRecords = listOf(
            FuelRecord(
                id = 1L,
                vehicleId = 1L,
                date = LocalDate.of(2026, 7, 15),
                odometer = 50000,
                volume = 35.0,
                amount = 6500,
                unitPrice = 186,
                fuelEconomy = 14.2,
            ),
            FuelRecord(
                id = 2L,
                vehicleId = 1L,
                date = LocalDate.of(2026, 7, 1),
                odometer = 49500,
                volume = 33.5,
                amount = 6200,
                unitPrice = 185,
                fuelEconomy = 13.8,
            ),
        ),
        isLoading = false,
    )
    StylishMyVehiclesTheme {
        Surface(Modifier.padding(20.dp)) {
            androidx.compose.foundation.lazy.LazyColumn {
                FuelPeriodContent(
                    period = period,
                    state = state,
                    onEditRecord = {},
                    onRequestDelete = {},
                )
            }
        }
    }
}
