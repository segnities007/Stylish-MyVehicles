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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.segnities007.stylish_myvehicles.R
import com.segnities007.stylish_myvehicles.domain.model.MaintenanceSchedule
import com.segnities007.stylish_myvehicles.domain.model.Vehicle
import com.segnities007.stylish_myvehicles.domain.model.VehicleCategory
import com.segnities007.stylish_myvehicles.domain.repository.MaintenanceScheduleRepository
import com.segnities007.stylish_myvehicles.domain.repository.VehicleRepository
import com.segnities007.stylish_myvehicles.domain.usecase.vehicle.InsertVehicleUseCase
import com.segnities007.stylishui.components.atoms.StylishDialogSurface
import com.segnities007.stylishui.components.atoms.StylishFormTextField
import com.segnities007.stylishui.components.atoms.StylishIconButton
import com.segnities007.stylishui.components.atoms.StylishSectionTitle
import com.segnities007.stylishui.components.molecules.StylishConnectedChipRow
import com.segnities007.stylishui.components.molecules.StylishDatePickerField
import com.segnities007.stylishui.components.models.StylishConnectedChipItem
import com.segnities007.stylishui.components.organisms.StylishDialogActions
import com.segnities007.stylishui.components.patterns.StylishHeader
import com.segnities007.stylishui.components.patterns.StylishScaffold
import com.segnities007.stylish_myvehicles.presentation.theme.StylishMyVehiclesTheme
import kotlinx.coroutines.flow.flowOf
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate
import java.time.LocalDate

@Composable
fun VehicleEditScreen(
    viewModel: VehicleEditViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

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
                title = { Text(if (state.isEditing) stringResource(R.string.edit_vehicle) else stringResource(R.string.register_vehicle_title)) },
                navigation = {
                    StylishIconButton(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        stringResource(R.string.back),
                        onClick = { viewModel.accept(VehicleEditIntent.NavigateBack) },
                    )
                },
                actions = if (state.isEditing) {
                    {
                        StylishIconButton(
                            Icons.Default.Delete,
                            stringResource(R.string.delete),
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
                StylishSectionTitle(stringResource(R.string.vehicle_type))
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
                StylishSectionTitle(stringResource(R.string.basic_info))
                StylishFormTextField(
                    value = state.maker,
                    onValueChange = { viewModel.accept(VehicleEditIntent.MakerChanged(it)) },
                    label = stringResource(R.string.maker_label),
                    placeholder = "トヨタ",
                    isError = state.makerError != null,
                    errorMessage = state.makerError,
                )
                Spacer(Modifier.height(12.dp))
                StylishFormTextField(
                    value = state.name,
                    onValueChange = { viewModel.accept(VehicleEditIntent.NameChanged(it)) },
                    label = stringResource(R.string.model_name_label),
                    placeholder = "プリウス",
                    isError = state.nameError != null,
                    errorMessage = state.nameError,
                )
                Spacer(Modifier.height(12.dp))
                StylishFormTextField(
                    value = state.grade,
                    onValueChange = { viewModel.accept(VehicleEditIntent.GradeChanged(it)) },
                    label = stringResource(R.string.grade_label),
                    placeholder = "Z",
                )
                Spacer(Modifier.height(12.dp))
                StylishFormTextField(
                    value = state.year,
                    onValueChange = { viewModel.accept(VehicleEditIntent.YearChanged(it)) },
                    label = stringResource(R.string.year_label),
                    placeholder = "2022",
                    isError = state.yearError != null,
                    errorMessage = state.yearError,
                )
                Spacer(Modifier.height(12.dp))
                StylishFormTextField(
                    value = state.modelCode,
                    onValueChange = { viewModel.accept(VehicleEditIntent.ModelCodeChanged(it)) },
                    label = stringResource(R.string.model_code_label),
                    placeholder = "6AA-MXWH60",
                )
                Spacer(Modifier.height(12.dp))
                StylishFormTextField(
                    value = state.plateNumber,
                    onValueChange = { viewModel.accept(VehicleEditIntent.PlateNumberChanged(it)) },
                    label = stringResource(R.string.plate_number_label),
                    placeholder = "品川 330 あ 12-34",
                )
                Spacer(Modifier.height(12.dp))
                StylishFormTextField(
                    value = state.displacement,
                    onValueChange = { viewModel.accept(VehicleEditIntent.DisplacementChanged(it)) },
                    label = stringResource(R.string.displacement_label),
                    placeholder = "1800",
                    isError = state.displacementError != null,
                    errorMessage = state.displacementError,
                )
                Spacer(Modifier.height(12.dp))
                StylishFormTextField(
                    value = state.weight,
                    onValueChange = { viewModel.accept(VehicleEditIntent.WeightChanged(it)) },
                    label = stringResource(R.string.vehicle_weight_label),
                    placeholder = "1350",
                    isError = state.weightError != null,
                    errorMessage = state.weightError,
                )
                if (state.category == VehicleCategory.TRUCK) {
                    Spacer(Modifier.height(12.dp))
                    StylishFormTextField(
                        value = state.maxLoadKg,
                        onValueChange = { viewModel.accept(VehicleEditIntent.MaxLoadKgChanged(it)) },
                        label = stringResource(R.string.max_load_label),
                        placeholder = "2000",
                        isError = state.maxLoadKgError != null,
                        errorMessage = state.maxLoadKgError,
                    )
                }
                Spacer(Modifier.height(12.dp))
                StylishFormTextField(
                    value = state.color,
                    onValueChange = { viewModel.accept(VehicleEditIntent.ColorChanged(it)) },
                    label = stringResource(R.string.color_label),
                    placeholder = "ホワイトパール",
                )

                // ── 期限・保険 ──
                StylishSectionTitle(stringResource(R.string.deadline_insurance_section))
                StylishDatePickerField(
                    value = state.firstRegistrationDate?.toKotlinLocalDate(),
                    onValueChange = {
                        viewModel.accept(
                            VehicleEditIntent.FirstRegistrationDateChanged(
                                it?.toJavaLocalDate()
                            )
                        )
                    },
                    label = stringResource(R.string.first_registration_date_label),
                    confirmLabel = "OK",
                    dismissLabel = stringResource(R.string.cancel),
                    placeholder = stringResource(R.string.select_date),
                )
                Spacer(Modifier.height(12.dp))
                StylishDatePickerField(
                    value = state.inspectionExpiry?.toKotlinLocalDate(),
                    onValueChange = {
                        viewModel.accept(VehicleEditIntent.InspectionExpiryChanged(it?.toJavaLocalDate()))
                    },
                    label = stringResource(R.string.inspection_expiry_label),
                    confirmLabel = "OK",
                    dismissLabel = stringResource(R.string.cancel),
                    placeholder = stringResource(R.string.select_date),
                )
                Spacer(Modifier.height(12.dp))
                StylishDatePickerField(
                    value = state.jibaiExpiry?.toKotlinLocalDate(),
                    onValueChange = { viewModel.accept(VehicleEditIntent.JibaiExpiryChanged(it?.toJavaLocalDate())) },
                    label = stringResource(R.string.jibai_insurance_expiry_label),
                    confirmLabel = "OK",
                    dismissLabel = stringResource(R.string.cancel),
                    placeholder = stringResource(R.string.select_date),
                )
                Spacer(Modifier.height(12.dp))
                StylishDatePickerField(
                    value = state.insuranceExpiry?.toKotlinLocalDate(),
                    onValueChange = { viewModel.accept(VehicleEditIntent.InsuranceExpiryChanged(it?.toJavaLocalDate())) },
                    label = stringResource(R.string.voluntary_insurance_expiry_label),
                    confirmLabel = "OK",
                    dismissLabel = stringResource(R.string.cancel),
                    placeholder = stringResource(R.string.select_date),
                )
                Spacer(Modifier.height(12.dp))
                StylishFormTextField(
                    value = state.insuranceCompany,
                    onValueChange = { viewModel.accept(VehicleEditIntent.InsuranceCompanyChanged(it)) },
                    label = stringResource(R.string.insurance_company_label),
                    placeholder = "東京海上日動",
                )
                Spacer(Modifier.height(12.dp))
                StylishFormTextField(
                    value = state.insuranceRank,
                    onValueChange = { viewModel.accept(VehicleEditIntent.InsuranceRankChanged(it)) },
                    label = stringResource(R.string.insurance_rank_label),
                    placeholder = "20",
                    isError = state.insuranceRankError != null,
                    errorMessage = state.insuranceRankError,
                )

                // ── アクション ──
                Spacer(Modifier.height(24.dp))
                StylishDialogActions(
                    confirmLabel = if (state.isEditing) stringResource(R.string.update) else stringResource(R.string.register),
                    cancelLabel = stringResource(R.string.cancel),
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
                    stringResource(R.string.delete_vehicle),
                    style = MaterialTheme.typography.titleLarge,
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    stringResource(R.string.delete_vehicle_message),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(24.dp))
                StylishDialogActions(
                    confirmLabel = stringResource(R.string.delete),
                    cancelLabel = stringResource(R.string.cancel),
                    onConfirm = { viewModel.accept(VehicleEditIntent.ConfirmDelete) },
                    onCancel = { viewModel.accept(VehicleEditIntent.DismissDeleteDialog) },
                )
            }
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
                    override suspend fun deleteById(id: Long) {}
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
                    vehicleRepository = vehicleRepository,
                    insertVehicleUseCase = InsertVehicleUseCase(
                        vehicleRepository,
                        scheduleRepository
                    ),
                )
            }
            VehicleEditScreen(viewModel = viewModel, onNavigateBack = {})
        }
    }
}
