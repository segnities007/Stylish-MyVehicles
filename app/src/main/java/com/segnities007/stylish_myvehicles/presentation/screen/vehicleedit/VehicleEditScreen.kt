package com.segnities007.stylish_myvehicles.presentation.screen.vehicleedit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.segnities007.stylish_myvehicles.domain.model.MaintenanceSchedule
import com.segnities007.stylish_myvehicles.domain.model.Vehicle
import com.segnities007.stylish_myvehicles.domain.model.VehicleCategory
import com.segnities007.stylish_myvehicles.domain.repository.MaintenanceScheduleRepository
import com.segnities007.stylish_myvehicles.domain.repository.VehicleRepository
import com.segnities007.stylish_myvehicles.domain.usecase.vehicle.DeleteVehicleUseCase
import com.segnities007.stylish_myvehicles.domain.usecase.vehicle.GetVehicleUseCase
import com.segnities007.stylish_myvehicles.domain.usecase.vehicle.InsertVehicleUseCase
import com.segnities007.stylish_myvehicles.domain.usecase.vehicle.UpdateVehicleUseCase
import com.segnities007.stylish_myvehicles.presentation.components.atoms.StylishIconButton
import com.segnities007.stylish_myvehicles.presentation.components.molecules.StylishConnectedChipRow
import com.segnities007.stylish_myvehicles.presentation.components.molecules.StylishDatePickerField
import com.segnities007.stylish_myvehicles.presentation.components.molecules.StylishDialogActions
import com.segnities007.stylish_myvehicles.presentation.components.molecules.StylishDialogSurface
import com.segnities007.stylish_myvehicles.presentation.components.molecules.models.StylishConnectedChipItem
import com.segnities007.stylish_myvehicles.presentation.components.organisms.StylishHeader
import com.segnities007.stylish_myvehicles.presentation.components.organisms.StylishScaffold
import com.segnities007.stylish_myvehicles.presentation.components.organisms.StylishSectionTitle
import com.segnities007.stylish_myvehicles.presentation.theme.StylishMyVehiclesTheme
import kotlinx.coroutines.flow.flowOf
import java.time.LocalDate

@Composable
fun VehicleEditScreen(
    viewModel: VehicleEditViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is VehicleEditEffect.NavigateBack -> onNavigateBack()
            }
        }
    }

    StylishScaffold(
        modifier = modifier,
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
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
                }
                else {
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

                // ── 基本情報 ──
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
                StylishSectionTitle("期限・保険")
                StylishDatePickerField(
                    value = state.firstRegistrationDate,
                    onValueChange = {
                        viewModel.accept(
                            VehicleEditIntent.FirstRegistrationDateChanged(
                                it
                            )
                        )
                    },
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

@Preview(name = "VehicleEditScreen", showBackground = true, widthDp = 393)
@Composable
private fun VehicleEditScreenPreview() {
    StylishMyVehiclesTheme {
        Surface(Modifier.padding(20.dp)) {
            val vehicleRepository = remember {
                object : VehicleRepository {
                    override fun getAll() = flowOf<List<Vehicle>>(emptyList())
                    override fun getById(id: Long) = flowOf(
                        Vehicle(
                            id = 1,
                            category = VehicleCategory.CAR,
                            maker = "トヨタ",
                            name = "プリウス",
                            grade = "Z",
                            year = 2022,
                            modelCode = "MXWH60",
                            plateNumber = "品川 330 あ 12-34",
                            displacement = 1800,
                            weight = 1350,
                            color = "ホワイトパール",
                            firstRegistrationDate = LocalDate.of(2022, 4, 1),
                            jibaiExpiry = LocalDate.of(2028, 4, 1),
                            insuranceExpiry = LocalDate.of(2026, 4, 1),
                            insuranceCompany = "東京海上日動",
                            insuranceRank = 20,
                        ),
                    )

                    override suspend fun insert(vehicle: Vehicle) = 0L
                    override suspend fun update(vehicle: Vehicle) {}
                    override suspend fun delete(vehicle: Vehicle) {}
                }
            }
            val scheduleRepository = remember {
                object : MaintenanceScheduleRepository {
                    override fun getByVehicleId(vehicleId: Long) = flowOf<List<MaintenanceSchedule>>(emptyList())
                    override suspend fun getAll() = emptyList<MaintenanceSchedule>()
                    override suspend fun insertDefaults(
                        vehicleId: Long,
                        category: VehicleCategory
                    ) {
                    }

                    override suspend fun update(schedule: MaintenanceSchedule) {}
                    override suspend fun updateLastDone(
                        vehicleId: Long,
                        category: String,
                        date: LocalDate,
                        odometer: Int?
                    ) {
                    }
                }
            }
            val viewModel = remember {
                VehicleEditViewModel(
                    vehicleId = 1L,
                    getVehicleUseCase = GetVehicleUseCase(vehicleRepository),
                    insertVehicleUseCase = InsertVehicleUseCase(
                        vehicleRepository,
                        scheduleRepository
                    ),
                    updateVehicleUseCase = UpdateVehicleUseCase(vehicleRepository),
                    deleteVehicleUseCase = DeleteVehicleUseCase(vehicleRepository),
                )
            }
            VehicleEditScreen(viewModel = viewModel, onNavigateBack = {})
        }
    }
}
