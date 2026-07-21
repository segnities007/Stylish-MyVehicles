package com.segnities007.stylish_mycars.presentation.screen.cost.components

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
import com.segnities007.stylish_mycars.domain.model.CostCategory
import com.segnities007.stylish_mycars.presentation.components.molecules.StylishConnectedChipRow
import com.segnities007.stylish_mycars.presentation.components.molecules.StylishDatePickerField
import com.segnities007.stylish_mycars.presentation.components.molecules.StylishDialogActions
import com.segnities007.stylish_mycars.presentation.components.molecules.StylishDialogSurface
import com.segnities007.stylish_mycars.presentation.components.molecules.models.StylishConnectedChipItem
import com.segnities007.stylish_mycars.presentation.screen.cost.CostListIntent
import com.segnities007.stylish_mycars.presentation.screen.cost.CostListUiState
import com.segnities007.stylish_mycars.presentation.theme.StylishMyCarsTheme

/** 費用記録の入力・編集ダイアログ。カテゴリ選択チップ付き。 */
@Composable
fun CostInputDialog(
    state: CostListUiState,
    onIntent: (CostListIntent) -> Unit,
) {
    StylishDialogSurface(onDismiss = { onIntent(CostListIntent.CloseDialog) }) {
        Column(Modifier.padding(24.dp)) {
            Text(
                if (state.isEditing) "費用記録を編集" else "費用を記録",
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
                value = state.inputDate,
                onValueChange = { it?.let { d -> onIntent(CostListIntent.InputDateChanged(d)) } },
                label = "日付",
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = state.inputTitle,
                onValueChange = { onIntent(CostListIntent.InputTitleChanged(it)) },
                label = { Text("項目 *") },
                placeholder = { Text("任意保険料") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = state.inputAmount,
                onValueChange = { onIntent(CostListIntent.InputAmountChanged(it)) },
                label = { Text("金額 (円) *") },
                placeholder = { Text("12000") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(24.dp))
            StylishDialogActions(
                confirmLabel = if (state.isEditing) "更新" else "保存",
                cancelLabel = "キャンセル",
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
    StylishMyCarsTheme {
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
