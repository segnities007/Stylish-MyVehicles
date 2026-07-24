package com.segnities007.stylish_myvehicles.presentation.screen.vehicledetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.segnities007.stylish_myvehicles.domain.model.CostCategory
import com.segnities007.stylish_myvehicles.domain.repository.CostRecordRepository
import com.segnities007.stylish_myvehicles.domain.repository.FuelRecordRepository
import com.segnities007.stylish_myvehicles.domain.repository.MaintenanceRecordRepository
import com.segnities007.stylish_myvehicles.domain.repository.VehicleRepository
import com.segnities007.stylish_myvehicles.domain.usecase.ExportDataUseCase
import com.segnities007.stylish_myvehicles.domain.usecase.ExportDocument
import com.segnities007.stylish_myvehicles.presentation.components.organisms.VehicleField
import com.segnities007.stylish_myvehicles.presentation.components.organisms.VehicleFieldInputType
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class VehicleDetailViewModel(
    private val vehicleId: Long,
    private val vehicleRepository: VehicleRepository,
    private val fuelRecordRepository: FuelRecordRepository,
    private val maintenanceRecordRepository: MaintenanceRecordRepository,
    private val costRecordRepository: CostRecordRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(VehicleDetailUiState())
    val uiState: StateFlow<VehicleDetailUiState> = _uiState.asStateFlow()

    private val _effects = Channel<VehicleDetailEffect>(Channel.BUFFERED)
    val effects: Flow<VehicleDetailEffect> = _effects.receiveAsFlow()

    init {
        viewModelScope.launch {
            vehicleRepository.getById(vehicleId).collect { vehicle ->
                _uiState.update { it.copy(vehicle = vehicle, isLoading = false) }
            }
        }
        viewModelScope.launch {
            costRecordRepository.getByVehicleId(vehicleId).collect { records ->
                val now = java.time.LocalDate.now()
                val taxPaidThisYear = records
                    .any { it.category == CostCategory.TAX && it.date.year == now.year }
                _uiState.update { it.copy(taxPaidThisYear = taxPaidThisYear) }
            }
        }
    }

    fun accept(intent: VehicleDetailIntent) {
        when (intent) {
            is VehicleDetailIntent.NavigateBack ->
                _effects.trySend(VehicleDetailEffect.NavigateBack)

            is VehicleDetailIntent.EditVehicle ->
                _effects.trySend(VehicleDetailEffect.NavigateToEdit(vehicleId))

            is VehicleDetailIntent.OpenCostList ->
                _effects.trySend(VehicleDetailEffect.NavigateToCost(vehicleId))

            is VehicleDetailIntent.ExportFuelCsv -> exportFuel()
            is VehicleDetailIntent.ExportMaintenanceCsv -> exportMaintenance()
            is VehicleDetailIntent.ExportCostCsv -> exportCost()

            is VehicleDetailIntent.OpenFieldEditor -> openFieldEditor(intent.field)
            is VehicleDetailIntent.CloseFieldEditor ->
                _uiState.update { it.copy(editingField = null) }

            is VehicleDetailIntent.FieldTextChanged ->
                _uiState.update { it.copy(fieldInputText = intent.value) }

            is VehicleDetailIntent.FieldDateChanged ->
                _uiState.update { it.copy(fieldInputDate = intent.value) }

            is VehicleDetailIntent.FieldCategoryChanged ->
                _uiState.update { it.copy(fieldInputCategory = intent.value) }

            is VehicleDetailIntent.SaveField -> saveField()
        }
    }

    private fun openFieldEditor(field: VehicleField) {
        val vehicle = _uiState.value.vehicle ?: return
        _uiState.update { state ->
            when (field.inputType) {
                VehicleFieldInputType.DATE -> state.copy(
                    editingField = field,
                    fieldInputDate = when (field) {
                        VehicleField.FIRST_REGISTRATION_DATE -> vehicle.firstRegistrationDate
                        VehicleField.INSPECTION_EXPIRY -> vehicle.inspectionExpiry
                        VehicleField.JIBAI_EXPIRY -> vehicle.jibaiExpiry
                        VehicleField.INSURANCE_EXPIRY -> vehicle.insuranceExpiry
                        else -> null
                    },
                )

                VehicleFieldInputType.SELECTION -> state.copy(
                    editingField = field,
                    fieldInputCategory = vehicle.category,
                )

                else -> state.copy(
                    editingField = field,
                    fieldInputText = when (field) {
                        VehicleField.MAKER -> vehicle.maker
                        VehicleField.NAME -> vehicle.name
                        VehicleField.GRADE -> vehicle.grade
                        VehicleField.YEAR -> vehicle.year?.toString() ?: ""
                        VehicleField.MODEL_CODE -> vehicle.modelCode
                        VehicleField.PLATE_NUMBER -> vehicle.plateNumber
                        VehicleField.VIN -> vehicle.vin
                        VehicleField.DISPLACEMENT -> vehicle.displacement?.toString() ?: ""
                        VehicleField.WEIGHT -> vehicle.weight?.toString() ?: ""
                        VehicleField.MAX_LOAD -> vehicle.maxLoadKg?.toString() ?: ""
                        VehicleField.COLOR -> vehicle.color
                        VehicleField.INSURANCE_COMPANY -> vehicle.insuranceCompany
                        VehicleField.INSURANCE_RANK -> vehicle.insuranceRank?.toString() ?: ""
                        VehicleField.MEMO -> vehicle.memo
                        else -> ""
                    },
                )
            }
        }
    }

    private fun saveField() {
        val state = _uiState.value
        val vehicle = state.vehicle ?: return
        val field = state.editingField ?: return
        val text = state.fieldInputText.trim()

        val updated = when (field) {
            VehicleField.CATEGORY -> vehicle.copy(category = state.fieldInputCategory)
            VehicleField.MAKER -> vehicle.copy(maker = text)
            VehicleField.NAME -> vehicle.copy(name = text)
            VehicleField.GRADE -> vehicle.copy(grade = text)
            VehicleField.YEAR -> vehicle.copy(year = text.toIntOrNull())
            VehicleField.MODEL_CODE -> vehicle.copy(modelCode = text)
            VehicleField.PLATE_NUMBER -> vehicle.copy(plateNumber = text)
            VehicleField.VIN -> vehicle.copy(vin = text)
            VehicleField.DISPLACEMENT -> vehicle.copy(displacement = text.toIntOrNull())
            VehicleField.WEIGHT -> vehicle.copy(weight = text.toIntOrNull())
            VehicleField.MAX_LOAD -> vehicle.copy(maxLoadKg = text.toIntOrNull())
            VehicleField.COLOR -> vehicle.copy(color = text)
            VehicleField.FIRST_REGISTRATION_DATE ->
                vehicle.copy(firstRegistrationDate = state.fieldInputDate)

            VehicleField.INSPECTION_EXPIRY ->
                vehicle.copy(inspectionExpiry = state.fieldInputDate)

            VehicleField.JIBAI_EXPIRY -> vehicle.copy(jibaiExpiry = state.fieldInputDate)
            VehicleField.INSURANCE_EXPIRY -> vehicle.copy(insuranceExpiry = state.fieldInputDate)
            VehicleField.INSURANCE_COMPANY -> vehicle.copy(insuranceCompany = text)
            VehicleField.INSURANCE_RANK -> vehicle.copy(insuranceRank = text.toIntOrNull())
            VehicleField.MEMO -> vehicle.copy(memo = text)
        }

        viewModelScope.launch {
            vehicleRepository.update(updated)
            _uiState.update { it.copy(editingField = null) }
        }
    }

    private fun exportFuel() {
        viewModelScope.launch {
            val records = fuelRecordRepository.getByVehicleId(vehicleId).first()
            _effects.send(
                VehicleDetailEffect.SaveDocument(
                    ExportDataUseCase.exportFuelRecordsCsv(records)
                )
            )
        }
    }

    private fun exportMaintenance() {
        viewModelScope.launch {
            val records = maintenanceRecordRepository.getByVehicleId(vehicleId).first()
            _effects.send(
                VehicleDetailEffect.SaveDocument(
                    ExportDataUseCase.exportMaintenanceRecordsCsv(records)
                )
            )
        }
    }

    private fun exportCost() {
        viewModelScope.launch {
            val records = costRecordRepository.getByVehicleId(vehicleId).first()
            _effects.send(
                VehicleDetailEffect.SaveDocument(
                    ExportDataUseCase.exportCostRecordsCsv(records)
                )
            )
        }
    }
}

sealed interface VehicleDetailEffect {
    data object NavigateBack : VehicleDetailEffect
    data class NavigateToEdit(val vehicleId: Long) : VehicleDetailEffect
    data class NavigateToCost(val vehicleId: Long) : VehicleDetailEffect
    data class SaveDocument(val document: ExportDocument) :
        VehicleDetailEffect
}
