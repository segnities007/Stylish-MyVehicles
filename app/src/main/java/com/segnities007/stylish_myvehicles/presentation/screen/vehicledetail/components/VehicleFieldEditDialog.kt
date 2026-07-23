package com.segnities007.stylish_myvehicles.presentation.screen.vehicledetail.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.segnities007.stylish_myvehicles.domain.model.VehicleCategory
import com.segnities007.stylish_myvehicles.presentation.components.molecules.StylishConnectedChipRow
import com.segnities007.stylish_myvehicles.presentation.components.molecules.StylishDatePickerField
import com.segnities007.stylish_myvehicles.presentation.components.molecules.StylishDialogActions
import com.segnities007.stylish_myvehicles.presentation.components.molecules.StylishDialogSurface
import com.segnities007.stylish_myvehicles.presentation.components.molecules.models.StylishConnectedChipItem
import com.segnities007.stylish_myvehicles.presentation.components.organisms.VehicleField
import com.segnities007.stylish_myvehicles.presentation.components.organisms.VehicleFieldInputType
import com.segnities007.stylish_myvehicles.presentation.theme.StylishMyVehiclesTheme
import java.time.LocalDate

/**
 * 車両情報のフィールドを1つだけ編集するダイアログ。
 * フィールドの [VehicleFieldInputType] に応じた入力UIを表示する。
 */
@Composable
fun VehicleFieldEditDialog(
    field: VehicleField,
    inputText: String,
    inputDate: LocalDate?,
    inputCategory: VehicleCategory,
    onTextChanged: (String) -> Unit,
    onDateChanged: (LocalDate?) -> Unit,
    onCategoryChanged: (VehicleCategory) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit,
) {
    StylishDialogSurface(onDismiss = onDismiss) {
        Column(Modifier.padding(24.dp)) {
            Text(field.label, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(16.dp))

            when (field.inputType) {
                VehicleFieldInputType.TEXT -> OutlinedTextField(
                    value = inputText,
                    onValueChange = onTextChanged,
                    label = { Text(field.label) },
                    singleLine = field != VehicleField.MEMO,
                    minLines = if (field == VehicleField.MEMO) 3 else 1,
                    modifier = Modifier.fillMaxWidth(),
                )

                VehicleFieldInputType.NUMBER -> OutlinedTextField(
                    value = inputText,
                    onValueChange = { value -> onTextChanged(value.filter { it.isDigit() }) },
                    label = {
                        Text(field.unit?.let { "${field.label} (${it})" } ?: field.label)
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                )

                VehicleFieldInputType.DATE -> StylishDatePickerField(
                    value = inputDate,
                    onValueChange = onDateChanged,
                    label = field.label,
                )

                VehicleFieldInputType.SELECTION -> StylishConnectedChipRow(
                    fillWidth = true,
                    items = VehicleCategory.entries.map { category ->
                        StylishConnectedChipItem(
                            label = category.label,
                            onClick = { onCategoryChanged(category) },
                            selected = inputCategory == category,
                        )
                    },
                )
            }

            Spacer(Modifier.height(24.dp))
            StylishDialogActions(
                confirmLabel = "保存",
                cancelLabel = "キャンセル",
                onConfirm = onSave,
                onCancel = onDismiss,
            )
        }
    }
}

@Preview(name = "VehicleFieldEditDialog", showBackground = true, widthDp = 393)
@Composable
private fun VehicleFieldEditDialogPreview() {
    StylishMyVehiclesTheme {
        VehicleFieldEditDialog(
            field = VehicleField.PLATE_NUMBER,
            inputText = "横浜 300 あ 12-34",
            inputDate = null,
            inputCategory = VehicleCategory.CAR,
            onTextChanged = {},
            onDateChanged = {},
            onCategoryChanged = {},
            onSave = {},
            onDismiss = {},
        )
    }
}
