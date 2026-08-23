package com.segnities007.stylish_myvehicles.presentation.screen.cost.components

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
import com.segnities007.stylish_myvehicles.domain.model.CostCategory
import com.segnities007.stylishui.components.atoms.StylishDialogSurface
import com.segnities007.stylishui.components.molecules.StylishConnectedChipRow
import com.segnities007.stylishui.components.molecules.StylishDatePickerField
import com.segnities007.stylishui.components.models.StylishConnectedChipItem
import com.segnities007.stylishui.components.organisms.StylishDialogActions
import com.segnities007.stylish_myvehicles.presentation.screen.cost.CostListIntent
import com.segnities007.stylish_myvehicles.presentation.screen.cost.CostListUiState
import com.segnities007.stylish_myvehicles.presentation.theme.StylishMyVehiclesTheme
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate

/** 費用記録の入力・編集ダイアログ。カテゴリ選択チップ付き。 */
@Composable
fun CostInputDialog(
    state: CostListUiState,
    onIntent: (CostListIntent) -> Unit,
) {
    StylishDialogSurface(onDismiss = { onIntent(CostListIntent.CloseDialog) }) {
        Column(Modifier.padding(24.dp)) {
            Text(
                if (state.isEditing) stringResource(R.string.edit_cost_record) else stringResource(R.string.add_cost_record),
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(Modifier.height(16.dp))

            StylishConnectedChipRow(
                items = CostCategory.entries.map { category ->
                    StylishConnectedChipItem(
                        label = category.label,
                        onClick = { onIntent(CostListIntent.InputCategoryChanged(category)) },
                        selected = state.inputCategory == category,
                    )
                },
            )
            Spacer(Modifier.height(16.dp))

            StylishDatePickerField(
                value = state.inputDate.toKotlinLocalDate(),
                onValueChange = { it?.let { d -> onIntent(CostListIntent.InputDateChanged(d.toJavaLocalDate())) } },
                label = stringResource(R.string.date_label),
                confirmLabel = "OK",
                dismissLabel = stringResource(R.string.cancel),
                placeholder = stringResource(R.string.select_date),
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = state.inputTitle,
                onValueChange = { onIntent(CostListIntent.InputTitleChanged(it)) },
                label = { Text(stringResource(R.string.cost_item_label)) },
                placeholder = { Text("任意保険料") },
                singleLine = true,
                isError = state.titleError != null,
                supportingText = state.titleError?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = state.inputAmount,
                onValueChange = { onIntent(CostListIntent.InputAmountChanged(it)) },
                label = { Text(stringResource(R.string.cost_amount_label)) },
                placeholder = { Text("12000") },
                singleLine = true,
                isError = state.amountError != null,
                supportingText = state.amountError?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(24.dp))
            StylishDialogActions(
                confirmLabel = if (state.isEditing) stringResource(R.string.update) else stringResource(R.string.save),
                cancelLabel = stringResource(R.string.cancel),
                onConfirm = { onIntent(CostListIntent.Save) },
                onCancel = { onIntent(CostListIntent.CloseDialog) },
                confirmEnabled = state.canSave,
            )
        }
    }
}

@Preview(name = "Cost input dialog", showBackground = true, widthDp = 393, heightDp = 650)
@Composable
private fun CostInputDialogPreview() {
    StylishMyVehiclesTheme {
        CostInputDialog(
            state = CostListUiState(
                inputCategory = CostCategory.INSURANCE,
                inputTitle = "任意保険料",
                inputAmount = "12000",
            ),
            onIntent = {},
        )
    }
}
