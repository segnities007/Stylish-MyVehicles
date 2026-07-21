package com.segnities007.stylish_myvehicles.presentation.screen.cost

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.segnities007.stylish_myvehicles.domain.model.CostCategory
import com.segnities007.stylish_myvehicles.domain.model.CostRecord
import com.segnities007.stylish_myvehicles.domain.repository.CostRecordRepository
import com.segnities007.stylish_myvehicles.domain.usecase.cost.DeleteCostRecordUseCase
import com.segnities007.stylish_myvehicles.domain.usecase.cost.GetCostRecordsUseCase
import com.segnities007.stylish_myvehicles.domain.usecase.cost.InsertCostRecordUseCase
import com.segnities007.stylish_myvehicles.domain.usecase.cost.UpdateCostRecordUseCase
import com.segnities007.stylish_myvehicles.presentation.components.atoms.StylishIconButton
import com.segnities007.stylish_myvehicles.presentation.components.molecules.StylishConnectedListItemColumn
import com.segnities007.stylish_myvehicles.presentation.components.molecules.StylishDeleteConfirmDialog
import com.segnities007.stylish_myvehicles.presentation.components.molecules.StylishEmptyState
import com.segnities007.stylish_myvehicles.presentation.components.molecules.models.StylishConnectedListItem
import com.segnities007.stylish_myvehicles.presentation.components.organisms.StylishHeader
import com.segnities007.stylish_myvehicles.presentation.components.organisms.StylishScaffold
import com.segnities007.stylish_myvehicles.presentation.screen.cost.components.CostInputDialog
import com.segnities007.stylish_myvehicles.presentation.screen.cost.components.CostSummarySection
import com.segnities007.stylish_myvehicles.presentation.theme.StylishMyVehiclesTheme
import kotlinx.coroutines.flow.flowOf
import java.time.LocalDate

@Composable
fun CostListScreen(
    viewModel: CostListViewModel,
    onNavigateBack: () -> Unit,
    openAddDialog: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsState()

    // 追加Dialog（記録を追加）から遷移してきた場合、すぐ入力Dialogを開く
    LaunchedEffect(openAddDialog) {
        if (openAddDialog) viewModel.accept(CostListIntent.OpenAddDialog)
    }

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is CostListEffect.NavigateBack -> onNavigateBack()
            }
        }
    }

    StylishScaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.accept(CostListIntent.OpenAddDialog) },
                containerColor = MaterialTheme.colorScheme.onSurface,
                contentColor = MaterialTheme.colorScheme.surface,
            ) {
                Icon(Icons.Default.Add, contentDescription = "費用を記録")
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
                title = { Text("費用") },
                navigation = {
                    StylishIconButton(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        "戻る",
                        onClick = { viewModel.accept(CostListIntent.NavigateBack) },
                    )
                },
            )

            CostSummarySection(
                state = state,
                onIntent = viewModel::accept,
                modifier = Modifier.padding(horizontal = 20.dp),
            )

            when {
                state.isLoading ->
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }

                state.filteredRecords.isEmpty() ->
                    StylishEmptyState(
                        icon = Icons.Default.AttachMoney,
                        title = "費用記録がありません",
                        description = "給油・整備の記録や下の＋ボタンから費用を追加できます",
                    )

                else -> {
                    val filtered = state.filteredRecords
                    StylishConnectedListItemColumn(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        spacing = 4.dp,
                        items = filtered.map { record ->
                            StylishConnectedListItem(
                                headline = record.title,
                                supportingText = "${record.date} / ${record.category.label}",
                                onClick = { viewModel.accept(CostListIntent.EditRecord(record.id)) },
                                onLongClick = {
                                    viewModel.accept(CostListIntent.RequestDelete(record.id))
                                },
                                trailingContent = {
                                    Text(
                                        "${String.format("%,d", record.amount)}円",
                                        style = MaterialTheme.typography.titleMedium,
                                    )
                                },
                            )
                        },
                    )
                }
            }
        }
    }

    if (state.isDialogOpen) {
        CostInputDialog(state = state, onIntent = viewModel::accept)
    }

    if (state.deletingRecordId != null) {
        StylishDeleteConfirmDialog(
            title = "費用記録を削除",
            message = "この費用記録を削除しますか？この操作は取り消せません。",
            onConfirm = { viewModel.accept(CostListIntent.ConfirmDelete) },
            onDismiss = { viewModel.accept(CostListIntent.DismissDelete) },
        )
    }
}

@Preview(name = "CostListScreen", showBackground = true, widthDp = 393)
@Composable
private fun CostListScreenPreview() {
    StylishMyVehiclesTheme {
        Surface() {
            val repository = remember {
                object : CostRecordRepository {
                    override fun getByVehicleId(vehicleId: Long) = flowOf(
                        listOf(
                            CostRecord(
                                1,
                                1,
                                LocalDate.of(2026, 7, 15),
                                CostCategory.FUEL,
                                "給油",
                                5000
                            ),
                            CostRecord(
                                2,
                                1,
                                LocalDate.of(2026, 7, 10),
                                CostCategory.PARKING,
                                "駐車場",
                                1500
                            ),
                            CostRecord(
                                3,
                                1,
                                LocalDate.of(2026, 7, 5),
                                CostCategory.MAINTENANCE,
                                "オイル交換",
                                8000
                            ),
                        ),
                    )

                    override fun getByVehicleIdAndDateRange(
                        vehicleId: Long,
                        start: LocalDate,
                        end: LocalDate
                    ) = flowOf<List<CostRecord>>(emptyList())

                    override suspend fun insert(record: CostRecord) = 0L
                    override suspend fun update(record: CostRecord) {}
                    override suspend fun delete(record: CostRecord) {}
                }
            }
            val viewModel = remember {
                CostListViewModel(
                    vehicleId = 1L,
                    getCostRecordsUseCase = GetCostRecordsUseCase(repository),
                    insertCostRecordUseCase = InsertCostRecordUseCase(repository),
                    updateCostRecordUseCase = UpdateCostRecordUseCase(repository),
                    deleteCostRecordUseCase = DeleteCostRecordUseCase(repository),
                )
            }
            CostListScreen(viewModel = viewModel, onNavigateBack = {})
        }
    }
}
