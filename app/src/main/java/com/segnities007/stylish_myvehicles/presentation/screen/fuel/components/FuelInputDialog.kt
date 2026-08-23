package com.segnities007.stylish_myvehicles.presentation.screen.fuel.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.segnities007.stylish_myvehicles.R
import com.segnities007.stylishui.components.atoms.StylishDialogSurface
import com.segnities007.stylishui.components.molecules.StylishDatePickerField
import com.segnities007.stylishui.components.organisms.StylishDialogActions
import com.segnities007.stylish_myvehicles.presentation.screen.fuel.FuelRecordIntent
import com.segnities007.stylish_myvehicles.presentation.screen.fuel.FuelRecordUiState
import com.segnities007.stylish_myvehicles.presentation.theme.StylishMyVehiclesTheme
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate

/** 給油記録の入力・編集ダイアログ。レシート読み取りによる自動入力に対応。 */
@Composable
fun FuelInputDialog(
    state: FuelRecordUiState,
    onIntent: (FuelRecordIntent) -> Unit,
    onScanReceipt: () -> Unit,
) {
    StylishDialogSurface(onDismiss = { onIntent(FuelRecordIntent.CloseDialog) }) {
        Column(Modifier.padding(24.dp)) {
            Text(
                if (state.isEditing) stringResource(R.string.edit_fuel_record) else stringResource(R.string.add_fuel_record),
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(Modifier.height(16.dp))

            OutlinedButton(
                onClick = onScanReceipt,
                enabled = !state.isScanning,
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (state.isScanning) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.reading_receipt))
                }
                else {
                    Icon(Icons.Default.CameraAlt, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.scan_receipt_auto_input))
                }
            }
            Spacer(Modifier.height(16.dp))

            StylishDatePickerField(
                value = state.inputDate.toKotlinLocalDate(),
                onValueChange = { it?.let { d -> onIntent(FuelRecordIntent.DateChanged(d.toJavaLocalDate())) } },
                label = stringResource(R.string.date_label),
                confirmLabel = "OK",
                dismissLabel = stringResource(R.string.cancel),
                placeholder = stringResource(R.string.select_date),
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = state.inputOdometer,
                onValueChange = { onIntent(FuelRecordIntent.OdometerChanged(it)) },
                label = { Text(stringResource(R.string.odometer_km_label)) },
                placeholder = { Text("45230") },
                singleLine = true,
                isError = state.odometerError != null,
                supportingText = state.odometerError?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = state.inputVolume,
                onValueChange = { onIntent(FuelRecordIntent.VolumeChanged(it)) },
                label = { Text(stringResource(R.string.fuel_volume_label)) },
                placeholder = { Text("32.5") },
                singleLine = true,
                isError = state.volumeError != null,
                supportingText = state.volumeError?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = state.inputAmount,
                onValueChange = { onIntent(FuelRecordIntent.AmountChanged(it)) },
                label = { Text(stringResource(R.string.amount_yen_label)) },
                placeholder = { Text("5688") },
                singleLine = true,
                isError = state.amountError != null,
                supportingText = state.amountError?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(12.dp))
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    stringResource(R.string.full_tank),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = state.inputIsFullTank,
                    onCheckedChange = { onIntent(FuelRecordIntent.FullTankChanged(it)) },
                )
            }

            state.calculatedEconomy?.let { economy ->
                Spacer(Modifier.height(8.dp))
                Text(
                    stringResource(R.string.fuel_economy_display, economy),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            Spacer(Modifier.height(24.dp))
            StylishDialogActions(
                confirmLabel = if (state.isEditing) stringResource(R.string.update) else stringResource(R.string.save),
                cancelLabel = stringResource(R.string.cancel),
                onConfirm = { onIntent(FuelRecordIntent.Save) },
                onCancel = { onIntent(FuelRecordIntent.CloseDialog) },
                confirmEnabled = state.canSave,
            )
        }
    }
}

@Preview(name = "Fuel input dialog", showBackground = true, widthDp = 393, heightDp = 700)
@Composable
private fun FuelInputDialogPreview() {
    StylishMyVehiclesTheme {
        FuelInputDialog(
            state = FuelRecordUiState(
                inputOdometer = "45230",
                inputVolume = "32.5",
                inputAmount = "5688",
                calculatedEconomy = "18.5 km/L",
            ),
            onIntent = {},
            onScanReceipt = {},
        )
    }
}
