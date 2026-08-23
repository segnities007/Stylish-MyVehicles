package com.segnities007.stylish_myvehicles.presentation.screen.maintenance.components

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.segnities007.stylish_myvehicles.R
import com.segnities007.stylish_myvehicles.domain.model.MaintenanceCategory
import com.segnities007.stylishui.components.atoms.StylishDialogSurface
import com.segnities007.stylishui.components.molecules.StylishConnectedChipRow
import com.segnities007.stylishui.components.molecules.StylishDatePickerField
import com.segnities007.stylishui.components.models.StylishConnectedChipItem
import com.segnities007.stylishui.components.organisms.StylishDialogActions
import com.segnities007.stylish_myvehicles.presentation.screen.maintenance.MaintenanceRecordIntent
import com.segnities007.stylish_myvehicles.presentation.screen.maintenance.MaintenanceRecordUiState
import com.segnities007.stylish_myvehicles.presentation.theme.StylishMyVehiclesTheme
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate

/** 整備記録の入力・編集ダイアログ。カテゴリ選択チップ付き。 */
@Composable
fun MaintenanceInputDialog(
    state: MaintenanceRecordUiState,
    onIntent: (MaintenanceRecordIntent) -> Unit,
) {
    StylishDialogSurface(onDismiss = { onIntent(MaintenanceRecordIntent.CloseDialog) }) {
        Column(Modifier.padding(24.dp)) {
            Text(
                if (state.isEditing) stringResource(R.string.edit_maintenance_record) else stringResource(R.string.add_maintenance_record),
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(Modifier.height(16.dp))

            StylishConnectedChipRow(
                items = state.relevantCategories.map { category ->
                    StylishConnectedChipItem(
                        label = category.label,
                        onClick = { onIntent(MaintenanceRecordIntent.CategoryChanged(category)) },
                        selected = state.inputCategory == category,
                    )
                },
            )
            Spacer(Modifier.height(16.dp))

            StylishDatePickerField(
                value = state.inputDate.toKotlinLocalDate(),
                onValueChange = { it?.let { d -> onIntent(MaintenanceRecordIntent.DateChanged(d.toJavaLocalDate())) } },
                label = stringResource(R.string.date_label),
                confirmLabel = "OK",
                dismissLabel = stringResource(R.string.cancel),
                placeholder = stringResource(R.string.select_date),
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = state.inputTitle,
                onValueChange = { onIntent(MaintenanceRecordIntent.TitleChanged(it)) },
                label = { Text(stringResource(R.string.maintenance_content_label)) },
                placeholder = { Text("エンジンオイル交換") },
                singleLine = true,
                isError = state.titleError != null,
                supportingText = state.titleError?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = state.inputOdometer,
                onValueChange = { onIntent(MaintenanceRecordIntent.OdometerChanged(it)) },
                label = { Text(stringResource(R.string.maintenance_odometer_label)) },
                placeholder = { Text("45230") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = state.inputCost,
                onValueChange = { onIntent(MaintenanceRecordIntent.CostChanged(it)) },
                label = { Text(stringResource(R.string.maintenance_cost_label)) },
                placeholder = { Text("4200") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = state.inputShopName,
                onValueChange = { onIntent(MaintenanceRecordIntent.ShopNameChanged(it)) },
                label = { Text(stringResource(R.string.maintenance_shop_label)) },
                placeholder = { Text("ディーラー") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(12.dp))

            Spacer(Modifier.height(24.dp))
            StylishDialogActions(
                confirmLabel = if (state.isEditing) stringResource(R.string.update) else stringResource(R.string.save),
                cancelLabel = stringResource(R.string.cancel),
                onConfirm = { onIntent(MaintenanceRecordIntent.Save) },
                onCancel = { onIntent(MaintenanceRecordIntent.CloseDialog) },
                confirmEnabled = state.canSave,
            )
        }
    }
}

@Preview(name = "Maintenance input dialog", showBackground = true, widthDp = 393, heightDp = 700)
@Composable
private fun MaintenanceInputDialogPreview() {
    StylishMyVehiclesTheme {
        MaintenanceInputDialog(
            state = MaintenanceRecordUiState(
                inputCategory = MaintenanceCategory.OIL,
                inputTitle = "エンジンオイル交換",
                inputCost = "4200",
            ),
            onIntent = {},
        )
    }
}
