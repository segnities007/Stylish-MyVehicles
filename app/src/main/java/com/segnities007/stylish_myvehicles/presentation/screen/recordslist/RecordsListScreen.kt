package com.segnities007.stylish_myvehicles.presentation.screen.recordslist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.segnities007.stylish_myvehicles.domain.model.CostCategory
import com.segnities007.stylish_myvehicles.domain.model.CostRecord
import com.segnities007.stylish_myvehicles.domain.model.FuelRecord
import com.segnities007.stylish_myvehicles.domain.model.MaintenanceCategory
import com.segnities007.stylish_myvehicles.domain.model.MaintenanceRecord
import com.segnities007.stylish_myvehicles.presentation.components.atoms.StylishIconButton
import com.segnities007.stylish_myvehicles.presentation.components.molecules.StylishConnectedListItemColumn
import com.segnities007.stylish_myvehicles.presentation.components.molecules.StylishEmptyState
import com.segnities007.stylish_myvehicles.presentation.components.molecules.models.StylishConnectedListItem
import com.segnities007.stylish_myvehicles.presentation.components.organisms.StylishHeader
import com.segnities007.stylish_myvehicles.presentation.components.organisms.StylishPageContent
import com.segnities007.stylish_myvehicles.presentation.components.organisms.StylishScaffold
import com.segnities007.stylish_myvehicles.presentation.theme.StylishMyVehiclesTheme
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

/** 全記録（給油・整備・費用）を時系列で一覧表示する画面。ヘッダーはスクロールに追従する。 */
@Composable
fun RecordsListScreen(
    viewModel: RecordsListViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToFuel: (Long) -> Unit,
    onNavigateToMaintenance: (Long) -> Unit,
    onNavigateToCost: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is RecordsListEffect.NavigateBack -> onNavigateBack()
                is RecordsListEffect.OpenFuel -> onNavigateToFuel(effect.vehicleId)
                is RecordsListEffect.OpenMaintenance -> onNavigateToMaintenance(effect.vehicleId)
                is RecordsListEffect.OpenCost -> onNavigateToCost(effect.vehicleId)
            }
        }
    }

    StylishScaffold(modifier = modifier) {
        when {
            state.isLoading ->
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }

            else -> StylishPageContent(
                header = {
                    StylishHeader(
                        title = { Text("記録一覧") },
                        navigation = {
                            StylishIconButton(
                                Icons.AutoMirrored.Filled.ArrowBack, "戻る",
                                onClick = { viewModel.accept(RecordsListIntent.NavigateBack) },
                            )
                        },
                    )
                },
            ) {
                if (state.sections.isEmpty()) {
                    item {
                        StylishEmptyState(
                            icon = Icons.AutoMirrored.Filled.ReceiptLong,
                            title = "記録がありません",
                            description = "給油・整備・費用を記録しましょう",
                        )
                    }
                }
                else {
                    state.sections.forEach { section ->
                        item(key = "header_${section.month}") {
                            MonthHeader(section)
                        }
                        item(key = "list_${section.month}") {
                            Column {
                                StylishConnectedListItemColumn(
                                    spacing = 4.dp,
                                    items = section.entries.map { entry ->
                                        toListItem(entry, viewModel::accept)
                                    },
                                )
                                Spacer(Modifier.height(16.dp))
                            }
                        }
                    }
                    item { Spacer(Modifier.height(24.dp)) }
                }
            }
        }
    }
}

@Composable
private fun MonthHeader(section: RecordSection) {
    Column {
        Spacer(Modifier.height(8.dp))
        Row(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                section.month.format(DateTimeFormatter.ofPattern("yyyy年M月")),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            Text(
                "${String.format("%,d", section.totalAmount)}円",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

private fun toListItem(
    entry: RecordEntry,
    onIntent: (RecordsListIntent) -> Unit,
): StylishConnectedListItem = when (entry) {
    is FuelEntry -> {
        val r = entry.record
        StylishConnectedListItem(
            headline = buildList {
                add("給油 ${r.volume}L")
                r.fuelEconomy?.let { add("%.1f km/L".format(it)) }
            }.joinToString(" / "),
            supportingText = r.date.toString(),
            onClick = { onIntent(RecordsListIntent.OpenRecord(entry)) },
            leadingContent = {
                Icon(
                    Icons.Default.LocalGasStation, null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            },
            trailingContent = {
                Text(
                    "${String.format("%,d", r.amount)}円",
                    style = MaterialTheme.typography.titleMedium,
                )
            },
        )
    }

    is MaintenanceEntry -> {
        val r = entry.record
        StylishConnectedListItem(
            headline = r.title,
            supportingText = "${r.date} / ${r.category.label}",
            onClick = { onIntent(RecordsListIntent.OpenRecord(entry)) },
            leadingContent = {
                Icon(
                    Icons.Default.Build, null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            },
            trailingContent = if (r.cost > 0) {
                {
                    Text(
                        "${String.format("%,d", r.cost)}円",
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            }
            else {
                null
            },
        )
    }

    is CostEntry -> {
        val r = entry.record
        StylishConnectedListItem(
            headline = r.title,
            supportingText = "${r.date} / ${r.category.label}",
            onClick = { onIntent(RecordsListIntent.OpenRecord(entry)) },
            leadingContent = {
                Icon(
                    Icons.Default.AttachMoney, null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            },
            trailingContent = {
                Text(
                    "${String.format("%,d", r.amount)}円",
                    style = MaterialTheme.typography.titleMedium,
                )
            },
        )
    }
}

@Preview(name = "RecordsListScreen", showBackground = true, widthDp = 393)
@Composable
private fun RecordsListScreenPreview() {
    val section = RecordSection(
        month = YearMonth.of(2026, 7),
        entries = listOf(
            FuelEntry(
                FuelRecord(
                    id = 1, vehicleId = 1, date = LocalDate.of(2026, 7, 15),
                    odometer = 45230, volume = 40.0, amount = 6800, fuelEconomy = 15.5,
                )
            ),
            MaintenanceEntry(
                MaintenanceRecord(
                    id = 2, vehicleId = 1, date = LocalDate.of(2026, 7, 10),
                    category = MaintenanceCategory.OIL, title = "エンジンオイル交換", cost = 5500,
                )
            ),
            CostEntry(
                CostRecord(
                    id = 3, vehicleId = 1, date = LocalDate.of(2026, 7, 1),
                    category = CostCategory.TAX, title = "自動車税", amount = 36000,
                )
            ),
        ),
    )
    StylishMyVehiclesTheme {
        Surface(Modifier.padding(20.dp)) {
            Column {
                MonthHeader(section)
                StylishConnectedListItemColumn(
                    spacing = 4.dp,
                    items = section.entries.map { toListItem(it) {} },
                )
            }
        }
    }
}
