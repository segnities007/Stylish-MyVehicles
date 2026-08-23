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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.segnities007.stylish_myvehicles.R
import com.segnities007.stylish_myvehicles.domain.model.VehicleCategory
import com.segnities007.stylishui.components.atoms.StylishDialogSurface
import com.segnities007.stylishui.components.molecules.StylishConnectedChipRow
import com.segnities007.stylishui.components.molecules.StylishDatePickerField
import com.segnities007.stylishui.components.models.StylishConnectedChipItem
import com.segnities007.stylishui.components.organisms.StylishDialogActions
import com.segnities007.stylish_myvehicles.presentation.components.organisms.VehicleField
import com.segnities007.stylish_myvehicles.presentation.components.organisms.VehicleFieldInputType
import com.segnities007.stylish_myvehicles.presentation.theme.StylishMyVehiclesTheme
import java.time.LocalDate
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate

/**
 * 車両情報のフィールドを1つだけ編集するダイアログ。
 * フィールドの [VehicleFieldInputType] に応じた入力UIを表示する。
 */
@Composable
fun VehicleFieldEditDialog(
    field: VehicleField,
    value: VehicleFieldEditValue,
    actions: VehicleFieldEditActions,
) {
    StylishDialogSurface(onDismiss = actions::dismiss) {
        Column(Modifier.padding(24.dp)) {
            Text(field.label, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(16.dp))

            when (field.inputType) {
                VehicleFieldInputType.TEXT -> OutlinedTextField(
                    value = value.inputText,
                    onValueChange = actions::onTextChanged,
                    label = { Text(field.label) },
                    singleLine = field != VehicleField.MEMO,
                    minLines = if (field == VehicleField.MEMO) 3 else 1,
                    modifier = Modifier.fillMaxWidth(),
                )

                VehicleFieldInputType.NUMBER -> OutlinedTextField(
                    value = value.inputText,
                    onValueChange = { text ->
                        actions.onTextChanged(text.filter { it.isDigit() })
                    },
                    label = {
                        Text(field.unit?.let { "${field.label} (${it})" } ?: field.label)
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                )

                VehicleFieldInputType.DATE -> StylishDatePickerField(
                    value = value.inputDate?.toKotlinLocalDate(),
                    onValueChange = { date -> actions.onDateChanged(date?.toJavaLocalDate()) },
                    label = field.label,
                    confirmLabel = "OK",
                    dismissLabel = stringResource(R.string.cancel),
                    placeholder = stringResource(R.string.select_date),
                )

                VehicleFieldInputType.SELECTION -> StylishConnectedChipRow(
                    fillWidth = true,
                    items = VehicleCategory.entries.map { category ->
                        StylishConnectedChipItem(
                            label = category.label,
                            onClick = { actions.onCategoryChanged(category) },
                            selected = value.inputCategory == category,
                        )
                    },
                )
            }

            Spacer(Modifier.height(24.dp))
            StylishDialogActions(
                confirmLabel = stringResource(R.string.save),
                cancelLabel = stringResource(R.string.cancel),
                onConfirm = actions::save,
                onCancel = actions::dismiss,
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
            value = VehicleFieldEditValue(
                inputText = "横浜 300 あ 12-34",
                inputDate = null,
                inputCategory = VehicleCategory.CAR,
            ),
            actions = EmptyVehicleFieldEditActions,
        )
    }
}

private val EmptyVehicleFieldEditActions = object : VehicleFieldEditActions {
    override fun onTextChanged(text: String) = Unit
    override fun onDateChanged(date: LocalDate?) = Unit
    override fun onCategoryChanged(category: VehicleCategory) = Unit
    override fun save() = Unit
    override fun dismiss() = Unit
}
