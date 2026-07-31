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
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.segnities007.stylish_myvehicles.R
import com.segnities007.stylish_myvehicles.data.trip.TripTrackingService
import com.segnities007.stylish_myvehicles.domain.model.TripPurpose
import com.segnities007.stylish_myvehicles.domain.model.TripRecord
import com.segnities007.stylishui.components.atoms.StylishIconButton
import com.segnities007.stylishui.components.atoms.StylishFab
import com.segnities007.stylishui.foundation.connectedColumnCorners
import com.segnities007.stylishui.foundation.connectedColumnEdges
import com.segnities007.stylishui.foundation.connectedShape
import com.segnities007.stylishui.components.molecules.StylishConnectedCard
import com.segnities007.stylishui.components.molecules.StylishDialogActions
import com.segnities007.stylishui.components.molecules.StylishDialogSurface
import com.segnities007.stylishui.components.molecules.StylishEmptyState
import com.segnities007.stylishui.components.molecules.StylishFormTextField
import com.segnities007.stylishui.components.molecules.StylishDatePickerField
import com.segnities007.stylishui.components.molecules.StylishConnectedChipRow
import com.segnities007.stylishui.components.models.StylishConnectedChipItem
import com.segnities007.stylishui.components.patterns.StylishHeader
import com.segnities007.stylishui.components.patterns.StylishScaffold
import com.segnities007.stylish_myvehicles.presentation.theme.StylishMyVehiclesTheme
import androidx.compose.ui.tooling.preview.Preview
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun TripRecordsScreen(
    vehicleId: Long,
    viewModel: TripRecordViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
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
                    contentDescription = stringResource(R.string.manual_record_trip),
                    onClick = { viewModel.accept(TripRecordIntent.OpenManualAdd) },
                )
            }
        },
    ) {
        Column(Modifier.fillMaxSize()) {
            StylishHeader(
                modifier = Modifier.padding(horizontal = 20.dp),
                title = { Text(stringResource(R.string.trip_records_title)) },
                navigation = {
                    StylishIconButton(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        stringResource(R.string.back),
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
                verticalArrangement = Arrangement.spacedBy(4.dp),
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
                        stringResource(R.string.trip_history),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(Modifier.height(8.dp))
                }

                if (state.records.none { !it.isRecording }) {
                    item {
                        StylishEmptyState(
                            title = stringResource(R.string.no_trip_records_title),
                            description = stringResource(R.string.no_trip_records_description),
                            icon = Icons.Default.Route,
                        )
                    }
                } else {
                    val completedRecords = state.records.filterNot { it.isRecording }
                    itemsIndexed(
                        items = completedRecords,
                        key = { _, record -> record.id },
                    ) { index, record ->
                        TripHistoryCard(
                            record = record,
                            index = index,
                            count = completedRecords.size,
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
                Text(stringResource(R.string.delete_trip_record), style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(12.dp))
                Text(
                    stringResource(R.string.delete_trip_record_message),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(24.dp))
                StylishDialogActions(
                    confirmLabel = stringResource(R.string.delete),
                    cancelLabel = stringResource(R.string.cancel),
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
                    activeRecord != null -> stringResource(R.string.drive_recording)
                    anotherVehicleIsRecording -> stringResource(R.string.another_vehicle_recording)
                    else -> stringResource(R.string.start_drive_recording)
                },
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                if (activeRecord != null) {
                    stringResource(
                        R.string.drive_distance_start_format,
                        activeRecord.distanceMeters / 1000.0,
                        activeRecord.startedAt.format(DateTimeFormatter.ofPattern("H:mm")),
                    )
                } else {
                    stringResource(R.string.location_only_while_recording)
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
                    Text(stringResource(R.string.stop_recording))
                }
            } else {
                Button(onClick = onStart, enabled = !anotherVehicleIsRecording) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Text(stringResource(R.string.start_drive))
                }
            }
        }
    }
}

@Composable
private fun TripHistoryCard(
    record: TripRecord,
    index: Int,
    count: Int,
    onEdit: () -> Unit,
) {
    val duration = record.endedAt?.let { Duration.between(record.startedAt, it) }
    val title = record.title.ifBlank { record.purpose.label }
    val details = buildList {
        add("%.1f km".format(record.distanceMeters / 1000.0))
        duration?.let { add(stringResource(R.string.duration_format, it.toMinutes() / 60, it.toMinutes() % 60)) }
        add(record.startedAt.format(DateTimeFormatter.ofPattern("M月d日 H:mm")))
    }.joinToString(" ・ ")
    val corners = connectedColumnCorners(index, count)
    StylishConnectedCard(
        title = title,
        supportingText = details,
        onClick = onEdit,
        onLongClick = onEdit,
        shape = connectedShape(corners),
        outlineEdges = connectedColumnEdges(index, count),
        outlineCorners = corners,
        trailingContent = {
            Icon(
                Icons.Default.Edit,
                contentDescription = stringResource(R.string.edit),
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
                    if (state.editingRecordId == null) stringResource(R.string.manual_record_trip) else stringResource(R.string.edit_trip_record),
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
                        Text(stringResource(R.string.delete), color = MaterialTheme.colorScheme.error)
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
                label = stringResource(R.string.date_label),
                confirmLabel = "OK",
                dismissLabel = stringResource(R.string.cancel),
                placeholder = stringResource(R.string.select_date),
            )
            Spacer(Modifier.height(12.dp))
            Text(
                stringResource(R.string.odometer_hint),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StylishFormTextField(
                    value = state.inputStartTime,
                    onValueChange = { onIntent(TripRecordIntent.StartTimeChanged(it)) },
                    label = stringResource(R.string.start_time_label),
                    placeholder = "09:30",
                    modifier = Modifier.weight(1f),
                )
                StylishFormTextField(
                    value = state.inputEndTime,
                    onValueChange = { onIntent(TripRecordIntent.EndTimeChanged(it)) },
                    label = stringResource(R.string.end_time_label),
                    placeholder = "11:00",
                    modifier = Modifier.weight(1f),
                )
            }
            Spacer(Modifier.height(12.dp))
            StylishFormTextField(
                value = state.inputDistanceKm,
                onValueChange = { onIntent(TripRecordIntent.DistanceChanged(it)) },
                label = stringResource(R.string.distance_km_label),
                placeholder = "86.4",
            )
            Spacer(Modifier.height(12.dp))
            StylishFormTextField(
                value = state.inputTitle,
                onValueChange = { onIntent(TripRecordIntent.TitleChanged(it)) },
                label = stringResource(R.string.title_label),
                placeholder = stringResource(R.string.trip_title_placeholder),
            )
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StylishFormTextField(
                    value = state.inputStartOdometer,
                    onValueChange = { onIntent(TripRecordIntent.StartOdometerChanged(it)) },
                    label = stringResource(R.string.start_odometer_label),
                    placeholder = "42100",
                    modifier = Modifier.weight(1f),
                )
                StylishFormTextField(
                    value = state.inputEndOdometer,
                    onValueChange = { onIntent(TripRecordIntent.EndOdometerChanged(it)) },
                    label = stringResource(R.string.end_odometer_label),
                    placeholder = "42186",
                    modifier = Modifier.weight(1f),
                )
            }
            Spacer(Modifier.height(20.dp))
            StylishDialogActions(
                confirmLabel = stringResource(R.string.save),
                cancelLabel = stringResource(R.string.cancel),
                onConfirm = { onIntent(TripRecordIntent.Save) },
                onCancel = { onIntent(TripRecordIntent.DismissEdit) },
                confirmEnabled = state.canSave,
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 393)
@Composable
private fun TripTrackingCardPreview() {
    StylishMyVehiclesTheme {
        TripTrackingCard(
            activeRecord = null,
            anotherVehicleIsRecording = false,
            onStart = {},
            onStop = {},
        )
    }
}

@Preview(showBackground = true, widthDp = 393)
@Composable
private fun TripHistoryCardPreview() {
    StylishMyVehiclesTheme {
        TripHistoryCard(
            record = TripRecord(
                id = 1,
                vehicleId = 1,
                title = "通勤",
                purpose = TripPurpose.COMMUTE,
                startedAt = LocalDateTime.of(2026, 7, 24, 9, 0),
                endedAt = LocalDateTime.of(2026, 7, 24, 10, 30),
                distanceMeters = 15_400,
            ),
            index = 0,
            count = 3,
            onEdit = {},
        )
    }
}
