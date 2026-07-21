package com.segnities007.stylish_mycars.presentation.screen.fuel

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.segnities007.stylish_mycars.data.ocr.ReceiptScanner
import com.segnities007.stylish_mycars.domain.model.FuelRecord
import com.segnities007.stylish_mycars.domain.model.RecordPeriod
import com.segnities007.stylish_mycars.presentation.components.atoms.StylishIconButton
import com.segnities007.stylish_mycars.presentation.components.atoms.utils.stylishConnectedColumnCorners
import com.segnities007.stylish_mycars.presentation.components.atoms.utils.stylishConnectedShape
import com.segnities007.stylish_mycars.presentation.components.charts.LineChartData
import com.segnities007.stylish_mycars.presentation.components.charts.SimpleLineChart
import com.segnities007.stylish_mycars.presentation.components.molecules.StylishConnectedCard
import com.segnities007.stylish_mycars.presentation.components.molecules.StylishConnectedChipRow
import com.segnities007.stylish_mycars.presentation.components.molecules.StylishDeleteConfirmDialog
import com.segnities007.stylish_mycars.presentation.components.molecules.StylishEmptyState
import com.segnities007.stylish_mycars.presentation.components.molecules.models.StylishConnectedChipItem
import com.segnities007.stylish_mycars.presentation.components.organisms.StylishHeader
import com.segnities007.stylish_mycars.presentation.screen.fuel.components.FuelInputDialog
import java.io.File
import kotlinx.coroutines.launch

@Composable
fun FuelRecordScreen(
    viewModel: FuelRecordViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var receiptImageUri by remember { mutableStateOf<Uri?>(null) }

    // カメラを起動して撮影し、その画像をレシート読み取りに使用する
    val receiptScanLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
    ) { success ->
        val uri = receiptImageUri
        if (success && uri != null) {
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
    }

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is FuelRecordEffect.NavigateBack -> onNavigateBack()
            }
        }
    }

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.accept(FuelRecordIntent.OpenAddDialog) },
                containerColor = MaterialTheme.colorScheme.onSurface,
                contentColor = MaterialTheme.colorScheme.surface,
            ) {
                Icon(Icons.Default.Add, contentDescription = "給油を記録")
            }
        },
    ) { innerPadding ->
        Column(Modifier.fillMaxSize().padding(innerPadding)) {
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
                items = RecordPeriod.entries.map { period ->
                    StylishConnectedChipItem(
                        label = period.label,
                        onClick = { viewModel.accept(FuelRecordIntent.SelectPeriod(period)) },
                        selected = state.selectedPeriod == period,
                    )
                },
            )

            val visibleRecords = state.filteredRecords

            // 燃費推移グラフ（2件以上の燃費データで表示）
            val economyData = visibleRecords
                .filter { it.fuelEconomy != null }
                .take(20)
                .reversed()
                .map { r ->
                    LineChartData("${r.date.monthValue}/${r.date.dayOfMonth}", r.fuelEconomy!!.toFloat())
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
                    LazyColumn(
                        Modifier.padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        items(visibleRecords.size) { index ->
                            val record = visibleRecords[index]
                            StylishConnectedCard(
                                title = buildRecordTitle(record),
                                supportingText = buildRecordSubtitle(record),
                                onClick = { viewModel.accept(FuelRecordIntent.EditRecord(record.id)) },
                                onLongClick = { viewModel.accept(FuelRecordIntent.RequestDelete(record.id)) },
                                shape = stylishConnectedShape(
                                    stylishConnectedColumnCorners(index, visibleRecords.size),
                                ),
                            )
                        }
                    }
            }
        }
    }

    if (state.isDialogOpen) {
        FuelInputDialog(
            state = state,
            onIntent = viewModel::accept,
            onScanReceipt = {
                val uri = createReceiptImageUri(context)
                receiptImageUri = uri
                receiptScanLauncher.launch(uri)
            },
        )
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
