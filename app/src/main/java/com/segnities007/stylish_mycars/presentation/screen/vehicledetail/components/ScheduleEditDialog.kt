package com.segnities007.stylish_mycars.presentation.screen.vehicledetail.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.segnities007.stylish_mycars.domain.model.MaintenanceCategory
import com.segnities007.stylish_mycars.domain.model.MaintenanceSchedule
import com.segnities007.stylish_mycars.presentation.components.molecules.StylishDatePickerField
import com.segnities007.stylish_mycars.presentation.components.molecules.StylishDialogActions
import com.segnities007.stylish_mycars.presentation.components.molecules.StylishDialogSurface
import com.segnities007.stylish_mycars.presentation.screen.vehicledetail.VehicleDetailIntent
import com.segnities007.stylish_mycars.presentation.screen.vehicledetail.VehicleDetailUiState
import com.segnities007.stylish_mycars.presentation.theme.StylishMyCarsTheme

/** メンテナンス目安の編集ダイアログ（間隔・前回実施日・前回実施時ODO）。 */
@Composable
fun ScheduleEditDialog(
    state: VehicleDetailUiState,
    onIntent: (VehicleDetailIntent) -> Unit,
) {
    val schedule = state.schedules.find { it.id == state.editingScheduleId }
    StylishDialogSurface(onDismiss = { onIntent(VehicleDetailIntent.CloseScheduleDialog) }) {
        Column(Modifier.padding(24.dp)) {
            Text(
                "${schedule?.category?.label ?: "メンテナンス"}の目安",
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = state.scheduleInputKm,
                onValueChange = { onIntent(VehicleDetailIntent.ScheduleIntervalKmChanged(it)) },
                label = { Text("走行距離の間隔 (km)") },
                placeholder = { Text("5000") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = state.scheduleInputMonths,
                onValueChange = { onIntent(VehicleDetailIntent.ScheduleIntervalMonthsChanged(it)) },
                label = { Text("期間の間隔 (ヶ月)") },
                placeholder = { Text("6") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(12.dp))
            StylishDatePickerField(
                value = state.scheduleInputLastDoneDate,
                onValueChange = { onIntent(VehicleDetailIntent.ScheduleLastDoneDateChanged(it)) },
                label = "前回実施日",
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = state.scheduleInputLastDoneOdo,
                onValueChange = { onIntent(VehicleDetailIntent.ScheduleLastDoneOdometerChanged(it)) },
                label = { Text("前回実施時の走行距離 (km)") },
                placeholder = { Text("40000") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(24.dp))
            StylishDialogActions(
                confirmLabel = "保存",
                cancelLabel = "キャンセル",
                onConfirm = { onIntent(VehicleDetailIntent.SaveSchedule) },
                onCancel = { onIntent(VehicleDetailIntent.CloseScheduleDialog) },
            )
        }
    }
}

@Preview(name = "Schedule edit dialog", showBackground = true, widthDp = 393, heightDp = 600)
@Composable
private fun ScheduleEditDialogPreview() {
    StylishMyCarsTheme {
        ScheduleEditDialog(
            state = VehicleDetailUiState(
                isScheduleDialogOpen = true,
                editingScheduleId = 1L,
                schedules = listOf(
                    MaintenanceSchedule(id = 1, vehicleId = 1, category = MaintenanceCategory.OIL),
                ),
                scheduleInputKm = "5000",
                scheduleInputMonths = "6",
            ),
            onIntent = {},
        )
    }
}
