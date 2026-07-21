package com.segnities007.stylish_mycars.presentation.screen.vehicleedit

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.segnities007.stylish_mycars.domain.model.VehicleCategory
import com.segnities007.stylish_mycars.presentation.components.atoms.StylishIconButton
import com.segnities007.stylish_mycars.presentation.components.molecules.StylishConnectedButtonRow
import com.segnities007.stylish_mycars.presentation.components.molecules.StylishConnectedChipRow
import com.segnities007.stylish_mycars.presentation.components.molecules.StylishDatePickerField
import com.segnities007.stylish_mycars.presentation.components.molecules.StylishDialogActions
import com.segnities007.stylish_mycars.presentation.components.molecules.StylishDialogSurface
import com.segnities007.stylish_mycars.presentation.components.molecules.models.StylishConnectedButtonItem
import com.segnities007.stylish_mycars.presentation.components.molecules.models.StylishConnectedChipItem
import com.segnities007.stylish_mycars.presentation.components.organisms.StylishHeader
import com.segnities007.stylish_mycars.presentation.components.organisms.StylishSectionTitle

@Composable
fun VehicleEditScreen(
    viewModel: VehicleEditViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsState()

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
    ) { uri ->
        viewModel.accept(VehicleEditIntent.PhotoUriChanged(uri?.toString()))
    }

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is VehicleEditEffect.NavigateBack -> onNavigateBack()
            }
        }
    }

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        Column(
            Modifier.fillMaxSize().padding(innerPadding).verticalScroll(rememberScrollState()),
        ) {
            StylishHeader(
                modifier = Modifier.padding(horizontal = 20.dp),
                title = { Text(if (state.isEditing) "車両を編集" else "車両を登録") },
                navigation = {
                    StylishIconButton(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        "戻る",
                        onClick = { viewModel.accept(VehicleEditIntent.NavigateBack) },
                    )
                },
                actions = if (state.isEditing) {
                    {
                        StylishIconButton(
                            Icons.Default.Delete,
                            "削除",
                            onClick = { viewModel.accept(VehicleEditIntent.RequestDelete) },
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer,
                        )
                    }
                } else {
                    null
                },
            )

            Column(Modifier.padding(horizontal = 20.dp)) {
                // 乗り物の種別
                StylishSectionTitle("種別")
                StylishConnectedChipRow(
                    items = VehicleCategory.entries.map { category ->
                        StylishConnectedChipItem(
                            label = category.label,
                            onClick = { viewModel.accept(VehicleEditIntent.CategoryChanged(category)) },
                            selected = state.category == category,
                        )
                    },
                )

                // 写真
                Spacer(Modifier.height(24.dp))
                StylishSectionTitle("写真")
                state.photoUri?.let { uri ->
                    AsyncImage(
                        model = uri,
                        contentDescription = "車両写真",
                        modifier = Modifier.fillMaxWidth().height(180.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop,
                    )
                    Spacer(Modifier.height(8.dp))
                }
                // 写真なし → 単体ボタン、写真あり → 隣接する[削除][変更]をConnectedで
                if (state.photoUri == null) {
                    OutlinedButton(
                        onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(Modifier.padding(start = 8.dp))
                        Text("写真を追加")
                    }
                } else {
                    StylishConnectedButtonRow(
                        items = listOf(
                            StylishConnectedButtonItem(
                                onClick = { viewModel.accept(VehicleEditIntent.PhotoUriChanged(null)) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer,
                                    contentColor = MaterialTheme.colorScheme.onErrorContainer,
                                ),
                                leadingContent = { Icon(Icons.Default.Delete, null) },
                            ) { Text("削除") },
                            StylishConnectedButtonItem(
                                onClick = {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                                    )
                                },
                            ) { Text("写真を変更") },
                        ),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                // ── 基本情報 ──
                Spacer(Modifier.height(24.dp))
                StylishSectionTitle("基本情報")
                TextField(
                    value = state.maker,
                    onValueChange = { viewModel.accept(VehicleEditIntent.MakerChanged(it)) },
                    label = "メーカー *",
                    placeholder = "トヨタ",
                    isError = state.makerError != null,
                    errorMessage = state.makerError,
                )
                Spacer(Modifier.height(12.dp))
                TextField(
                    value = state.name,
                    onValueChange = { viewModel.accept(VehicleEditIntent.NameChanged(it)) },
                    label = "車種名 *",
                    placeholder = "プリウス",
                    isError = state.nameError != null,
                    errorMessage = state.nameError,
                )
                Spacer(Modifier.height(12.dp))
                TextField(
                    value = state.grade,
                    onValueChange = { viewModel.accept(VehicleEditIntent.GradeChanged(it)) },
                    label = "グレード",
                    placeholder = "Z",
                )
                Spacer(Modifier.height(12.dp))
                TextField(
                    value = state.year,
                    onValueChange = { viewModel.accept(VehicleEditIntent.YearChanged(it)) },
                    label = "年式",
                    placeholder = "2022",
                    isError = state.yearError != null,
                    errorMessage = state.yearError,
                )
                Spacer(Modifier.height(12.dp))
                TextField(
                    value = state.plateNumber,
                    onValueChange = { viewModel.accept(VehicleEditIntent.PlateNumberChanged(it)) },
                    label = "ナンバープレート",
                    placeholder = "品川 330 あ 12-34",
                )
                Spacer(Modifier.height(12.dp))
                TextField(
                    value = state.displacement,
                    onValueChange = { viewModel.accept(VehicleEditIntent.DisplacementChanged(it)) },
                    label = "排気量 (cc)",
                    placeholder = "1800",
                    isError = state.displacementError != null,
                    errorMessage = state.displacementError,
                )
                Spacer(Modifier.height(12.dp))
                TextField(
                    value = state.weight,
                    onValueChange = { viewModel.accept(VehicleEditIntent.WeightChanged(it)) },
                    label = "車両重量 (kg)",
                    placeholder = "1350",
                    isError = state.weightError != null,
                    errorMessage = state.weightError,
                )
                if (state.category == VehicleCategory.TRUCK) {
                    Spacer(Modifier.height(12.dp))
                    TextField(
                        value = state.maxLoadKg,
                        onValueChange = { viewModel.accept(VehicleEditIntent.MaxLoadKgChanged(it)) },
                        label = "最大積載量 (kg)",
                        placeholder = "2000",
                        isError = state.maxLoadKgError != null,
                        errorMessage = state.maxLoadKgError,
                    )
                }
                Spacer(Modifier.height(12.dp))
                TextField(
                    value = state.color,
                    onValueChange = { viewModel.accept(VehicleEditIntent.ColorChanged(it)) },
                    label = "カラー",
                    placeholder = "ホワイトパール",
                )

                // ── 期限・保険 ──
                Spacer(Modifier.height(24.dp))
                StylishSectionTitle("期限・保険")
                StylishDatePickerField(
                    value = state.firstRegistrationDate,
                    onValueChange = { viewModel.accept(VehicleEditIntent.FirstRegistrationDateChanged(it)) },
                    label = "初度登録日（車検計算に使用）",
                )
                Spacer(Modifier.height(12.dp))
                StylishDatePickerField(
                    value = state.jibaiExpiry,
                    onValueChange = { viewModel.accept(VehicleEditIntent.JibaiExpiryChanged(it)) },
                    label = "自賠責保険 満期日",
                )
                Spacer(Modifier.height(12.dp))
                StylishDatePickerField(
                    value = state.insuranceExpiry,
                    onValueChange = { viewModel.accept(VehicleEditIntent.InsuranceExpiryChanged(it)) },
                    label = "任意保険 満期日",
                )
                Spacer(Modifier.height(12.dp))
                TextField(
                    value = state.insuranceCompany,
                    onValueChange = { viewModel.accept(VehicleEditIntent.InsuranceCompanyChanged(it)) },
                    label = "保険会社",
                    placeholder = "東京海上日動",
                )
                Spacer(Modifier.height(12.dp))
                TextField(
                    value = state.insuranceRank,
                    onValueChange = { viewModel.accept(VehicleEditIntent.InsuranceRankChanged(it)) },
                    label = "等級",
                    placeholder = "20",
                )
                Spacer(Modifier.height(12.dp))
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        "自動車税 納付済み",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f),
                    )
                    Switch(
                        checked = state.taxPaid,
                        onCheckedChange = { viewModel.accept(VehicleEditIntent.TaxPaidChanged(it)) },
                    )
                }

                // ── メモ ──
                Spacer(Modifier.height(24.dp))
                StylishSectionTitle("メモ")
                TextField(
                    value = state.memo,
                    onValueChange = { viewModel.accept(VehicleEditIntent.MemoChanged(it)) },
                    label = "メモ",
                    placeholder = "自由記入",
                    minLines = 3,
                )

                // ── アクション ──
                Spacer(Modifier.height(24.dp))
                StylishDialogActions(
                    confirmLabel = if (state.isEditing) "更新" else "登録",
                    cancelLabel = "キャンセル",
                    onConfirm = { viewModel.accept(VehicleEditIntent.Save) },
                    onCancel = { viewModel.accept(VehicleEditIntent.NavigateBack) },
                    confirmEnabled = state.canSave,
                )
                Spacer(Modifier.height(32.dp))
            }
        }
    }

    // 削除確認ダイアログ
    if (state.showDeleteDialog) {
        StylishDialogSurface(onDismiss = { viewModel.accept(VehicleEditIntent.DismissDeleteDialog) }) {
            Column(Modifier.padding(24.dp)) {
                Text(
                    "車両を削除",
                    style = MaterialTheme.typography.titleLarge,
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    "この車両と関連するすべての記録（給油・整備・費用）が削除されます。この操作は取り消せません。",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(24.dp))
                StylishDialogActions(
                    confirmLabel = "削除",
                    cancelLabel = "キャンセル",
                    onConfirm = { viewModel.accept(VehicleEditIntent.ConfirmDelete) },
                    onCancel = { viewModel.accept(VehicleEditIntent.DismissDeleteDialog) },
                )
            }
        }
    }
}

@Composable
private fun TextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    minLines: Int = 1,
    isError: Boolean = false,
    errorMessage: String? = null,
) {
    Column {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            placeholder = { Text(placeholder) },
            minLines = minLines,
            modifier = Modifier.fillMaxWidth(),
            singleLine = minLines == 1,
            isError = isError,
        )
        if (errorMessage != null) {
            Text(
                errorMessage,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp),
            )
        }
    }
}
