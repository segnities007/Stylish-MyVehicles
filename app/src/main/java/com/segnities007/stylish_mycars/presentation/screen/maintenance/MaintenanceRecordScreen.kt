package com.segnities007.stylish_mycars.presentation.screen.maintenance

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CameraAlt
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.segnities007.stylish_mycars.domain.model.MaintenanceRecord
import com.segnities007.stylish_mycars.domain.model.RecordPeriod
import com.segnities007.stylish_mycars.presentation.components.atoms.StylishIconButton
import com.segnities007.stylish_mycars.presentation.components.molecules.StylishConnectedChipRow
import com.segnities007.stylish_mycars.presentation.components.molecules.StylishConnectedListItemColumn
import com.segnities007.stylish_mycars.presentation.components.molecules.StylishDeleteConfirmDialog
import com.segnities007.stylish_mycars.presentation.components.molecules.StylishEmptyState
import com.segnities007.stylish_mycars.presentation.components.molecules.models.StylishConnectedChipItem
import com.segnities007.stylish_mycars.presentation.components.molecules.models.StylishConnectedListItem
import com.segnities007.stylish_mycars.presentation.components.organisms.StylishHeader
import com.segnities007.stylish_mycars.presentation.screen.maintenance.components.MaintenanceInputDialog
import com.segnities007.stylish_mycars.presentation.util.ImageCaptureHelper

@Composable
fun MaintenanceRecordScreen(
    viewModel: MaintenanceRecordViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var photoUri by remember { mutableStateOf<Uri?>(null) }

    val photoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
    ) { success ->
        if (success) {
            photoUri?.let { viewModel.accept(MaintenanceRecordIntent.PhotoChanged(it.toString())) }
        }
    }

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is MaintenanceRecordEffect.NavigateBack -> onNavigateBack()
            }
        }
    }

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.accept(MaintenanceRecordIntent.OpenAddDialog) },
                containerColor = MaterialTheme.colorScheme.onSurface,
                contentColor = MaterialTheme.colorScheme.surface,
            ) {
                Icon(Icons.Default.Add, contentDescription = "整備を記録")
            }
        },
    ) { innerPadding ->
        Column(Modifier.fillMaxSize().padding(innerPadding)) {
            StylishHeader(
                modifier = Modifier.padding(horizontal = 20.dp),
                title = { Text("整備記録") },
                navigation = {
                    StylishIconButton(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        "戻る",
                        onClick = { viewModel.accept(MaintenanceRecordIntent.NavigateBack) },
                    )
                },
            )

            // 期間フィルタ
            StylishConnectedChipRow(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                items = RecordPeriod.entries.map { period ->
                    StylishConnectedChipItem(
                        label = period.label,
                        onClick = { viewModel.accept(MaintenanceRecordIntent.SelectPeriod(period)) },
                        selected = state.selectedPeriod == period,
                    )
                },
            )

            val visibleRecords = state.filteredRecords

            when {
                state.isLoading ->
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                state.records.isEmpty() ->
                    StylishEmptyState(
                        icon = Icons.Default.Build,
                        title = "整備記録がありません",
                        description = "下の＋ボタンから整備を記録しましょう",
                    )
                visibleRecords.isEmpty() ->
                    StylishEmptyState(
                        icon = Icons.Default.Build,
                        title = "この期間の記録がありません",
                        description = "期間フィルタを変更してみてください",
                    )
                else ->
                    StylishConnectedListItemColumn(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        spacing = 4.dp,
                        items = visibleRecords.map { record ->
                            StylishConnectedListItem(
                                headline = record.title,
                                supportingText = buildSubtitle(record),
                                onClick = { viewModel.accept(MaintenanceRecordIntent.EditRecord(record.id)) },
                                onLongClick = {
                                    viewModel.accept(MaintenanceRecordIntent.RequestDelete(record.id))
                                },
                                trailingContent = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        if (record.photoUri != null) {
                                            Icon(
                                                Icons.Default.CameraAlt, null,
                                                modifier = Modifier.size(16.dp),
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            )
                                            Spacer(Modifier.width(4.dp))
                                        }
                                        Text(
                                            record.category.label,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                    }
                                },
                            )
                        },
                    )
            }
        }
    }

    if (state.isDialogOpen) {
        MaintenanceInputDialog(
            state = state,
            onIntent = viewModel::accept,
            onCapturePhoto = {
                val uri = ImageCaptureHelper.createImageUri(context, "maintenance")
                photoUri = uri
                photoLauncher.launch(uri)
            },
        )
    }

    if (state.deletingRecordId != null) {
        StylishDeleteConfirmDialog(
            title = "整備記録を削除",
            message = "この整備記録を削除しますか？この操作は取り消せません。",
            onConfirm = { viewModel.accept(MaintenanceRecordIntent.ConfirmDelete) },
            onDismiss = { viewModel.accept(MaintenanceRecordIntent.DismissDelete) },
        )
    }
}

private fun buildSubtitle(record: MaintenanceRecord): String {
    val parts = mutableListOf(record.date.toString())
    record.odometer?.let { parts.add("${String.format("%,d", it)}km") }
    if (record.cost > 0) parts.add("${String.format("%,d", record.cost)}円")
    record.shopName.takeIf { it.isNotBlank() }?.let { parts.add(it) }
    return parts.joinToString(" / ")
}
