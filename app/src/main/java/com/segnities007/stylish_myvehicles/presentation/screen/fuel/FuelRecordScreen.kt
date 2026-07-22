package com.segnities007.stylish_myvehicles.presentation.screen.fuel

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.segnities007.stylish_myvehicles.data.ocr.ReceiptScanner
import com.segnities007.stylish_myvehicles.domain.model.CostRecord
import com.segnities007.stylish_myvehicles.domain.model.FuelRecord
import com.segnities007.stylish_myvehicles.domain.model.RecordPeriod
import com.segnities007.stylish_myvehicles.domain.repository.CostRecordRepository
import com.segnities007.stylish_myvehicles.domain.repository.FuelRecordRepository
import com.segnities007.stylish_myvehicles.domain.usecase.fuel.DeleteFuelRecordUseCase
import com.segnities007.stylish_myvehicles.domain.usecase.fuel.GetFuelRecordsUseCase
import com.segnities007.stylish_myvehicles.domain.usecase.fuel.InsertFuelRecordUseCase
import com.segnities007.stylish_myvehicles.domain.usecase.fuel.UpdateFuelRecordUseCase
import com.segnities007.stylish_myvehicles.presentation.components.atoms.StylishIconButton
import com.segnities007.stylish_myvehicles.presentation.components.molecules.LineChartData
import com.segnities007.stylish_myvehicles.presentation.components.molecules.SimpleLineChart
import com.segnities007.stylish_myvehicles.presentation.components.molecules.StylishConnectedCardGrid
import com.segnities007.stylish_myvehicles.presentation.components.molecules.StylishConnectedChipRow
import com.segnities007.stylish_myvehicles.presentation.components.molecules.StylishConnectedListItemColumn
import com.segnities007.stylish_myvehicles.presentation.components.molecules.StylishDeleteConfirmDialog
import com.segnities007.stylish_myvehicles.presentation.components.molecules.StylishDialogSurface
import com.segnities007.stylish_myvehicles.presentation.components.molecules.StylishEmptyState
import com.segnities007.stylish_myvehicles.presentation.components.molecules.models.StylishConnectedCardItem
import com.segnities007.stylish_myvehicles.presentation.components.molecules.models.StylishConnectedChipItem
import com.segnities007.stylish_myvehicles.presentation.components.molecules.models.StylishConnectedListItem
import com.segnities007.stylish_myvehicles.presentation.components.organisms.StylishHeader
import com.segnities007.stylish_myvehicles.presentation.components.organisms.StylishScaffold
import com.segnities007.stylish_myvehicles.presentation.screen.fuel.components.FuelInputDialog
import com.segnities007.stylish_myvehicles.presentation.theme.StylishMyVehiclesTheme
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDate

@Composable
fun FuelRecordScreen(
    viewModel: FuelRecordViewModel,
    onNavigateBack: () -> Unit,
    openAddDialog: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var receiptImageUri by remember { mutableStateOf<Uri?>(null) }
    var showReceiptSourceDialog by remember { mutableStateOf(false) }

    // 追加Dialog（記録を追加）から遷移してきた場合、すぐ入力Dialogを開く
    LaunchedEffect(openAddDialog) {
        if (openAddDialog) viewModel.accept(FuelRecordIntent.OpenAddDialog)
    }

    // 取得した画像（カメラ撮影 or ギャラリー選択）をレシート読み取りにかける共通処理
    fun scanReceipt(uri: Uri) {
        viewModel.accept(FuelRecordIntent.ScanningChanged(true))
        scope.launch {
            val result = runCatching { ReceiptScanner.scan(context, uri) }
            result.onSuccess { data ->
                viewModel.accept(
                    FuelRecordIntent.ReceiptScanned(
                        volume = data.volume,
                        amount = data.amount,
                        odometer = data.odometer,
                    ),
                )
            }
            viewModel.accept(FuelRecordIntent.ScanningChanged(false))
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

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is FuelRecordEffect.NavigateBack -> onNavigateBack()
            }
        }
    }

    StylishScaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.accept(FuelRecordIntent.OpenAddDialog) },
                containerColor = MaterialTheme.colorScheme.onSurface,
                contentColor = MaterialTheme.colorScheme.surface,
            ) {
                Icon(Icons.Default.Add, contentDescription = "給油を記録")
            }
        },
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            StylishHeader(
                modifier = Modifier.padding(horizontal = 20.dp),
                title = { Text("給油記録") },
                navigation = {
                    StylishIconButton(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        "戻る",
                        onClick = { viewModel.accept(FuelRecordIntent.NavigateBack) },
                    )
                },
            )

            // 期間フィルタ
            StylishConnectedChipRow(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                fillWidth = true,
                items = RecordPeriod.entries.map { period ->
                    StylishConnectedChipItem(
                        label = period.label,
                        onClick = { viewModel.accept(FuelRecordIntent.SelectPeriod(period)) },
                        selected = state.selectedPeriod == period,
                    )
                },
            )

            val visibleRecords = state.filteredRecords

            // 期間サマリー
            if (visibleRecords.isNotEmpty()) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column {
                        Text(
                            "平均燃費",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            state.filteredAverageEconomy?.let { "%.1f km/L".format(it) } ?: "--",
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "給油量",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            "%.1f L".format(state.filteredTotalVolume),
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            "合計費用",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            "${String.format("%,d", state.filteredTotalAmount)}円",
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                }
            }

            // 燃費推移グラフ（2件以上の燃費データで表示）
            val economyData = remember {
                visibleRecords
                    .filter { it.fuelEconomy != null }
                    .take(20)
                    .reversed()
                    .map { r ->
                        LineChartData(
                            "${r.date.monthValue}/${r.date.dayOfMonth}",
                            r.fuelEconomy!!.toFloat()
                        )
                    }
            }
            if (economyData.size >= 2) {
                Column(Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                    Text(
                        "燃費推移 (km/L)",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(Modifier.height(8.dp))
                    SimpleLineChart(data = economyData)
                }
            }

            when {
                state.isLoading ->
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }

                state.records.isEmpty() ->
                    StylishEmptyState(
                        icon = Icons.Default.LocalGasStation,
                        title = "給油記録がありません",
                        description = "下の＋ボタンから給油を記録しましょう",
                    )

                visibleRecords.isEmpty() ->
                    StylishEmptyState(
                        icon = Icons.Default.LocalGasStation,
                        title = "この期間の記録がありません",
                        description = "期間フィルタを変更してみてください",
                    )

                else ->
                    StylishConnectedListItemColumn(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        spacing = 4.dp,
                        items = visibleRecords.map { record ->
                            StylishConnectedListItem(
                                headline = buildRecordTitle(record),
                                supportingText = buildRecordSubtitle(record),
                                onClick = { viewModel.accept(FuelRecordIntent.EditRecord(record.id)) },
                                onLongClick = {
                                    viewModel.accept(
                                        FuelRecordIntent.RequestDelete(
                                            record.id
                                        )
                                    )
                                },
                            )
                        },
                    )
            }
        }
    }

    if (state.isDialogOpen) {
        FuelInputDialog(
            state = state,
            onIntent = viewModel::accept,
            onScanReceipt = { showReceiptSourceDialog = true },
        )
    }

    // レシート画像の取得元（カメラ / 写真 / キャンセル）を選択するダイアログ
    if (showReceiptSourceDialog) {
        StylishDialogSurface(onDismiss = { showReceiptSourceDialog = false }) {
            Column(Modifier.padding(24.dp)) {
                Text("レシートを読み取る", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(16.dp))
                StylishConnectedCardGrid(
                    columns = 2,
                    spacing = 4.dp,
                    items = listOf(
                        StylishConnectedCardItem(
                            title = "カメラ",
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
                            title = "写真",
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
                            title = "キャンセル",
                            onClick = { showReceiptSourceDialog = false },
                        ),
                    ),
                )
            }
        }
    }

    if (state.deletingRecordId != null) {
        StylishDeleteConfirmDialog(
            title = "給油記録を削除",
            message = "この給油記録を削除しますか？この操作は取り消せません。",
            onConfirm = { viewModel.accept(FuelRecordIntent.ConfirmDelete) },
            onDismiss = { viewModel.accept(FuelRecordIntent.DismissDelete) },
        )
    }
}

private fun buildRecordTitle(record: FuelRecord): String {
    val parts = mutableListOf("${record.volume}L")
    record.fuelEconomy?.let { parts.add("%.1f km/L".format(it)) }
    return parts.joinToString(" / ")
}

private fun buildRecordSubtitle(record: FuelRecord): String {
    val parts = mutableListOf(
        record.date.toString(),
        "${String.format("%,d", record.amount)}円",
        "${String.format("%,d", record.odometer)}km",
    )
    record.unitPrice?.let { parts.add("${it}円/L") }
    if (!record.isFullTank) parts.add("満タン以外")
    return parts.joinToString(" / ")
}

/** カメラ撮影用の一時画像UriをFileProvider経由で作成する。 */
private fun createReceiptImageUri(context: Context): Uri {
    val dir = File(context.cacheDir, "receipt_images").apply { mkdirs() }
    val file = File.createTempFile("receipt_", ".jpg", dir)
    return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
}

@Preview(name = "FuelRecordScreen", showBackground = true, widthDp = 393)
@Composable
private fun FuelRecordScreenPreview() {
    StylishMyVehiclesTheme {
        Surface(Modifier.padding(20.dp)) {
            val repository = remember {
                object : FuelRecordRepository {
                    override fun getByVehicleId(vehicleId: Long) = flowOf(
                        listOf(
                            FuelRecord(
                                1,
                                1,
                                LocalDate.of(2026, 7, 15),
                                50000,
                                35.0,
                                6500,
                                unitPrice = 186,
                                fuelEconomy = 14.2
                            ),
                            FuelRecord(
                                2,
                                1,
                                LocalDate.of(2026, 7, 1),
                                49500,
                                33.5,
                                6200,
                                unitPrice = 185,
                                fuelEconomy = 13.8
                            ),
                        ),
                    )

                    override suspend fun getLatest(vehicleId: Long) = null
                    override suspend fun insert(record: FuelRecord) = 0L
                    override suspend fun update(record: FuelRecord) {}
                    override suspend fun delete(record: FuelRecord) {}
                }
            }
            val costRepository = remember {
                object : CostRecordRepository {
                    override fun getByVehicleId(vehicleId: Long) = flowOf<List<CostRecord>>(emptyList())
                    override fun getByVehicleIdAndDateRange(
                        vehicleId: Long, start: LocalDate, end: LocalDate
                    ) = flowOf<List<CostRecord>>(emptyList())
                    override suspend fun insert(record: CostRecord) = 0L
                    override suspend fun update(record: CostRecord) {}
                    override suspend fun delete(record: CostRecord) {}
                }
            }
            val viewModel = remember {
                FuelRecordViewModel(
                    vehicleId = 1L,
                    getFuelRecordsUseCase = GetFuelRecordsUseCase(repository),
                    insertFuelRecordUseCase = InsertFuelRecordUseCase(repository, costRepository),
                    updateFuelRecordUseCase = UpdateFuelRecordUseCase(repository),
                    deleteFuelRecordUseCase = DeleteFuelRecordUseCase(repository),
                )
            }
            FuelRecordScreen(viewModel = viewModel, onNavigateBack = {})
        }
    }
}
