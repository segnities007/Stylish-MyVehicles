package com.segnities007.stylish_mycars.presentation.screen.cost

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.segnities007.stylish_mycars.presentation.components.atoms.StylishIconButton
import com.segnities007.stylish_mycars.presentation.components.atoms.utils.stylishConnectedColumnCorners
import com.segnities007.stylish_mycars.presentation.components.atoms.utils.stylishConnectedShape
import com.segnities007.stylish_mycars.presentation.components.molecules.StylishConnectedCard
import com.segnities007.stylish_mycars.presentation.components.molecules.StylishDeleteConfirmDialog
import com.segnities007.stylish_mycars.presentation.components.molecules.StylishEmptyState
import com.segnities007.stylish_mycars.presentation.components.organisms.StylishHeader
import com.segnities007.stylish_mycars.presentation.screen.cost.components.CostInputDialog
import com.segnities007.stylish_mycars.presentation.screen.cost.components.CostSummarySection

@Composable
fun CostListScreen(
    viewModel: CostListViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is CostListEffect.NavigateBack -> onNavigateBack()
            }
        }
    }

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.accept(CostListIntent.OpenAddDialog) },
                containerColor = MaterialTheme.colorScheme.onSurface,
                contentColor = MaterialTheme.colorScheme.surface,
            ) {
                Icon(Icons.Default.Add, contentDescription = "費用を記録")
            }
        },
    ) { innerPadding ->
        Column(Modifier.fillMaxSize().padding(innerPadding)) {
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
                    LazyColumn(
                        Modifier.padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        items(filtered.size) { index ->
                            val record = filtered[index]
                            StylishConnectedCard(
                                title = record.title,
                                supportingText = "${record.date} / ${record.category.label}",
                                onClick = { viewModel.accept(CostListIntent.EditRecord(record.id)) },
                                onLongClick = {
                                    viewModel.accept(CostListIntent.RequestDelete(record.id))
                                },
                                shape = stylishConnectedShape(
                                    stylishConnectedColumnCorners(index, filtered.size),
                                ),
                                trailingContent = {
                                    Text(
                                        "${String.format("%,d", record.amount)}円",
                                        style = MaterialTheme.typography.titleMedium,
                                    )
                                },
                            )
                        }
                    }
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
