package com.segnities007.stylish_myvehicles.presentation.screen.trip

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.segnities007.stylish_myvehicles.data.trip.TripTrackingService
import com.segnities007.stylish_myvehicles.domain.model.TripPurpose
import com.segnities007.stylish_myvehicles.domain.model.TripRecord
import com.segnities007.stylish_myvehicles.presentation.components.atoms.StylishIconButton
import com.segnities007.stylish_myvehicles.presentation.components.atoms.StylishFab
import com.segnities007.stylish_myvehicles.presentation.components.molecules.StylishConnectedCard
import com.segnities007.stylish_myvehicles.presentation.components.molecules.StylishDialogActions
import com.segnities007.stylish_myvehicles.presentation.components.molecules.StylishDialogSurface
import com.segnities007.stylish_myvehicles.presentation.components.molecules.StylishEmptyState
import com.segnities007.stylish_myvehicles.presentation.components.molecules.StylishFormTextField
import com.segnities007.stylish_myvehicles.presentation.components.molecules.StylishDatePickerField
import com.segnities007.stylish_myvehicles.presentation.components.molecules.StylishConnectedChipRow
import com.segnities007.stylish_myvehicles.presentation.components.molecules.models.StylishConnectedChipItem
import com.segnities007.stylish_myvehicles.presentation.components.organisms.StylishHeader
import com.segnities007.stylish_myvehicles.presentation.components.organisms.StylishScaffold
import com.segnities007.stylish_myvehicles.presentation.theme.StylishMyVehiclesTheme
import java.time.Duration
import java.time.format.DateTimeFormatter

@Composable
fun TripRecordsScreen(
    vehicleId: Long,
    viewModel: TripRecordViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val listState = rememberLazyListState()
    val isAtTop by androidx.compose.runtime.remember(listState) {
        derivedStateOf {
            listState.firstVisibleItemIndex == 0 &&
                listState.firstVisibleItemScrollOffset <= 0
        }
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
            TripTrackingService.start(context, vehicleId)
        }
    }

    fun startTracking() {
        val hasLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED
        if (hasLocation) {
            TripTrackingService.start(context, vehicleId)
        } else {
            val permissions = buildList {
                add(Manifest.permission.ACCESS_FINE_LOCATION)
                add(Manifest.permission.ACCESS_COARSE_LOCATION)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    add(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
            permissionLauncher.launch(permissions.toTypedArray())
        }
    }

    StylishScaffold(
        modifier = modifier,
        floatingActionButton = {
            AnimatedVisibility(
                visible = isAtTop,
                enter = fadeIn(tween(200)) + slideInVertically(tween(200)) { it / 2 },
                exit = fadeOut(tween(200)) + slideOutVertically(tween(200)) { it / 2 },
            ) {
                StylishFab(
                    imageVector = Icons.Default.Add,
                    contentDescription = "移動を手動で記録",
                    onClick = { viewModel.accept(TripRecordIntent.OpenManualAdd) },
                )
            }
        },
    ) {
        Column(Modifier.fillMaxSize()) {
            StylishHeader(
                modifier = Modifier.padding(horizontal = 20.dp),
                title = { Text("移動記録") },
                navigation = {
                    StylishIconButton(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        "戻る",
                        onClick = onNavigateBack,
                    )
                },
            )

            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    start = 20.dp,
                    end = 20.dp,
                    bottom = 120.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item {
                    TripTrackingCard(
                        activeRecord = state.activeRecord,
                        anotherVehicleIsRecording =
                            TripTrackingService.isRecording(context) &&
                                TripTrackingService.recordingVehicleId(context) != vehicleId,
                        onStart = ::startTracking,
                        onStop = { TripTrackingService.stop(context) },
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "履歴",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }

                if (state.records.none { !it.isRecording }) {
                    item {
                        StylishEmptyState(
                            title = "移動記録はまだありません",
                            description = "ドライブを開始すると、距離と時間をここに記録します",
                            icon = Icons.Default.Route,
                        )
                    }
                } else {
                    items(
                        items = state.records.filterNot { it.isRecording },
                        key = { it.id },
                    ) { record ->
                        TripHistoryCard(
                            record = record,
                            onEdit = { viewModel.accept(TripRecordIntent.Edit(record.id)) },
                        )
                    }
                }
            }
        }
    }

    if (state.isInputDialogOpen) {
        TripEditDialog(
            state = state,
            onIntent = viewModel::accept,
        )
    }
    if (state.deletingRecordId != null) {
        StylishDialogSurface(
            onDismiss = { viewModel.accept(TripRecordIntent.DismissDelete) },
        ) {
            Column(Modifier.padding(24.dp)) {
                Text("移動記録を削除", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(12.dp))
                Text(
                    "保存されたルートを含め、この移動記録を削除します。",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(24.dp))
                StylishDialogActions(
                    confirmLabel = "削除",
                    cancelLabel = "キャンセル",
                    onConfirm = { viewModel.accept(TripRecordIntent.ConfirmDelete) },
                    onCancel = { viewModel.accept(TripRecordIntent.DismissDelete) },
                )
            }
        }
    }
}

@Composable
private fun TripTrackingCard(
    activeRecord: TripRecord?,
    anotherVehicleIsRecording: Boolean,
    onStart: () -> Unit,
    onStop: () -> Unit,
) {
    Surface(
        shape = MaterialTheme.shapes.extraLarge,
        color = if (activeRecord != null) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surfaceContainerLow
        },
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                imageVector = if (activeRecord != null) Icons.Default.Route else Icons.Default.PlayArrow,
                contentDescription = null,
                tint = if (activeRecord != null) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.primary
                },
            )
            Spacer(Modifier.height(12.dp))
            Text(
                when {
                    activeRecord != null -> "ドライブを記録中"
                    anotherVehicleIsRecording -> "別の車両で記録中"
                    else -> "ドライブを記録"
                },
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                if (activeRecord != null) {
                    "%.1f km ・ %s開始".format(
                        activeRecord.distanceMeters / 1000.0,
                        activeRecord.startedAt.format(DateTimeFormatter.ofPattern("H:mm")),
                    )
                } else {
                    "記録中だけ位置情報を使用します"
                },
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(20.dp))
            if (activeRecord != null) {
                Button(
                    onClick = onStop,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError,
                    ),
                ) {
                    Icon(Icons.Default.Stop, contentDescription = null)
                    Text(" 記録を終了")
                }
            } else {
                Button(onClick = onStart, enabled = !anotherVehicleIsRecording) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Text(" ドライブを開始")
                }
            }
        }
    }
}

@Composable
private fun TripHistoryCard(record: TripRecord, onEdit: () -> Unit) {
    val duration = record.endedAt?.let { Duration.between(record.startedAt, it) }
    val title = record.title.ifBlank { record.purpose.label }
    val details = buildList {
        add("%.1f km".format(record.distanceMeters / 1000.0))
        duration?.let { add("${it.toMinutes() / 60}時間${it.toMinutes() % 60}分") }
        add(record.startedAt.format(DateTimeFormatter.ofPattern("M月d日 H:mm")))
    }.joinToString(" ・ ")
    StylishConnectedCard(
        title = title,
        supportingText = details,
        onClick = onEdit,
        onLongClick = onEdit,
        trailingContent = {
            Icon(
                Icons.Default.Edit,
                contentDescription = "編集",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
    )
}

@Composable
private fun TripEditDialog(
    state: TripRecordUiState,
    onIntent: (TripRecordIntent) -> Unit,
) {
    StylishDialogSurface(onDismiss = { onIntent(TripRecordIntent.DismissEdit) }) {
        Column(Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    if (state.editingRecordId == null) "移動を手動で記録" else "移動記録を編集",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f),
                )
                if (state.editingRecordId != null) {
                    TextButton(
                        onClick = {
                            val id = state.editingRecordId ?: return@TextButton
                            onIntent(TripRecordIntent.DismissEdit)
                            onIntent(TripRecordIntent.RequestDelete(id))
                        },
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null)
                        Text("削除", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            StylishConnectedChipRow(
                items = TripPurpose.entries.map { purpose ->
                    StylishConnectedChipItem(
                        label = purpose.label,
                        selected = state.inputPurpose == purpose,
                        onClick = { onIntent(TripRecordIntent.PurposeChanged(purpose)) },
                    )
                },
            )
            Spacer(Modifier.height(12.dp))
            StylishDatePickerField(
                value = state.inputDate,
                onValueChange = { date ->
                    date?.let { onIntent(TripRecordIntent.DateChanged(it)) }
                },
                label = "日付",
            )
            Spacer(Modifier.height(12.dp))
            Text(
                "車のメーターに表示されている総走行距離です（任意）",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StylishFormTextField(
                    value = state.inputStartTime,
                    onValueChange = { onIntent(TripRecordIntent.StartTimeChanged(it)) },
                    label = "開始時刻",
                    placeholder = "09:30",
                    modifier = Modifier.weight(1f),
                )
                StylishFormTextField(
                    value = state.inputEndTime,
                    onValueChange = { onIntent(TripRecordIntent.EndTimeChanged(it)) },
                    label = "終了時刻",
                    placeholder = "11:00",
                    modifier = Modifier.weight(1f),
                )
            }
            Spacer(Modifier.height(12.dp))
            StylishFormTextField(
                value = state.inputDistanceKm,
                onValueChange = { onIntent(TripRecordIntent.DistanceChanged(it)) },
                label = "走行距離 (km)",
                placeholder = "86.4",
            )
            Spacer(Modifier.height(12.dp))
            StylishFormTextField(
                value = state.inputTitle,
                onValueChange = { onIntent(TripRecordIntent.TitleChanged(it)) },
                label = "タイトル",
                placeholder = "週末のドライブ",
            )
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StylishFormTextField(
                    value = state.inputStartOdometer,
                    onValueChange = { onIntent(TripRecordIntent.StartOdometerChanged(it)) },
                    label = "出発時の総走行距離 (km)",
                    placeholder = "42100",
                    modifier = Modifier.weight(1f),
                )
                StylishFormTextField(
                    value = state.inputEndOdometer,
                    onValueChange = { onIntent(TripRecordIntent.EndOdometerChanged(it)) },
                    label = "到着時の総走行距離 (km)",
                    placeholder = "42186",
                    modifier = Modifier.weight(1f),
                )
            }
            Spacer(Modifier.height(20.dp))
            StylishDialogActions(
                confirmLabel = "保存",
                cancelLabel = "キャンセル",
                onConfirm = { onIntent(TripRecordIntent.Save) },
                onCancel = { onIntent(TripRecordIntent.DismissEdit) },
                confirmEnabled = state.canSave,
            )
        }
    }
}
