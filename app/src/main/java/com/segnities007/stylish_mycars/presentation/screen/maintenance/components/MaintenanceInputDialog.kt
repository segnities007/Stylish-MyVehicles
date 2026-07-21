package com.segnities007.stylish_mycars.presentation.screen.maintenance.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.segnities007.stylish_mycars.domain.model.MaintenanceCategory
import com.segnities007.stylish_mycars.presentation.components.molecules.StylishConnectedChipRow
import com.segnities007.stylish_mycars.presentation.components.molecules.StylishDatePickerField
import com.segnities007.stylish_mycars.presentation.components.molecules.StylishDialogActions
import com.segnities007.stylish_mycars.presentation.components.molecules.StylishDialogSurface
import com.segnities007.stylish_mycars.presentation.components.molecules.models.StylishConnectedChipItem
import com.segnities007.stylish_mycars.presentation.screen.maintenance.MaintenanceRecordIntent
import com.segnities007.stylish_mycars.presentation.screen.maintenance.MaintenanceRecordUiState
import com.segnities007.stylish_mycars.presentation.theme.StylishMyCarsTheme

/** 整備記録の入力・編集ダイアログ。カテゴリ選択チップ付き。 */
@Composable
fun MaintenanceInputDialog(
    state: MaintenanceRecordUiState,
    onIntent: (MaintenanceRecordIntent) -> Unit,
    onCapturePhoto: () -> Unit,
) {
    StylishDialogSurface(onDismiss = { onIntent(MaintenanceRecordIntent.CloseDialog) }) {
        Column(Modifier.padding(24.dp)) {
            Text(
                if (state.isEditing) "整備記録を編集" else "整備を記録",
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
                value = state.inputDate,
                onValueChange = { it?.let { d -> onIntent(MaintenanceRecordIntent.DateChanged(d)) } },
                label = "日付",
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = state.inputTitle,
                onValueChange = { onIntent(MaintenanceRecordIntent.TitleChanged(it)) },
                label = { Text("整備内容 *") },
                placeholder = { Text("エンジンオイル交換") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = state.inputOdometer,
                onValueChange = { onIntent(MaintenanceRecordIntent.OdometerChanged(it)) },
                label = { Text("走行距離 (km)") },
                placeholder = { Text("45230") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = state.inputCost,
                onValueChange = { onIntent(MaintenanceRecordIntent.CostChanged(it)) },
                label = { Text("費用 (円)") },
                placeholder = { Text("4200") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = state.inputShopName,
                onValueChange = { onIntent(MaintenanceRecordIntent.ShopNameChanged(it)) },
                label = { Text("整備工場") },
                placeholder = { Text("ディーラー") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(12.dp))

            // 写真添付
            if (state.inputPhotoUri != null) {
                AsyncImage(
                    model = state.inputPhotoUri,
                    contentDescription = "添付写真",
                    modifier = Modifier.fillMaxWidth().height(160.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop,
                )
                Spacer(Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { onIntent(MaintenanceRecordIntent.PhotoChanged(null)) },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("写真を削除")
                }
            } else {
                OutlinedButton(
                    onClick = onCapturePhoto,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("写真を添付")
                }
            }

            Spacer(Modifier.height(24.dp))
            StylishDialogActions(
                confirmLabel = if (state.isEditing) "更新" else "保存",
                cancelLabel = "キャンセル",
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
    StylishMyCarsTheme {
        MaintenanceInputDialog(
            state = MaintenanceRecordUiState(
                inputCategory = MaintenanceCategory.OIL,
                inputTitle = "エンジンオイル交換",
                inputCost = "4200",
            ),
            onIntent = {},
            onCapturePhoto = {},
        )
    }
}
