package com.segnities007.stylish_mycars.presentation.screen.fuel.components

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
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.segnities007.stylish_mycars.presentation.components.molecules.StylishDatePickerField
import com.segnities007.stylish_mycars.presentation.components.molecules.StylishDialogActions
import com.segnities007.stylish_mycars.presentation.components.molecules.StylishDialogSurface
import com.segnities007.stylish_mycars.presentation.screen.fuel.FuelRecordIntent
import com.segnities007.stylish_mycars.presentation.screen.fuel.FuelRecordUiState
import com.segnities007.stylish_mycars.presentation.theme.StylishMyCarsTheme

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
                if (state.isEditing) "給油記録を編集" else "給油を記録",
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
                    Text("読み取り中…")
                } else {
                    Icon(Icons.Default.CameraAlt, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("レシートを読み取って自動入力")
                }
            }
            Spacer(Modifier.height(16.dp))

            StylishDatePickerField(
                value = state.inputDate,
                onValueChange = { it?.let { d -> onIntent(FuelRecordIntent.DateChanged(d)) } },
                label = "日付",
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = state.inputOdometer,
                onValueChange = { onIntent(FuelRecordIntent.OdometerChanged(it)) },
                label = { Text("走行距離 (km)") },
                placeholder = { Text("45230") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = state.inputVolume,
                onValueChange = { onIntent(FuelRecordIntent.VolumeChanged(it)) },
                label = { Text("給油量 (L)") },
                placeholder = { Text("32.5") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = state.inputAmount,
                onValueChange = { onIntent(FuelRecordIntent.AmountChanged(it)) },
                label = { Text("金額 (円)") },
                placeholder = { Text("5688") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(12.dp))
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("満タン", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                Switch(
                    checked = state.inputIsFullTank,
                    onCheckedChange = { onIntent(FuelRecordIntent.FullTankChanged(it)) },
                )
            }

            state.calculatedEconomy?.let { economy ->
                Spacer(Modifier.height(8.dp))
                Text(
                    "燃費: $economy",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            Spacer(Modifier.height(24.dp))
            StylishDialogActions(
                confirmLabel = if (state.isEditing) "更新" else "保存",
                cancelLabel = "キャンセル",
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
    StylishMyCarsTheme {
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
