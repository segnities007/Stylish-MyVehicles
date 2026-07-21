package com.segnities007.stylish_mycars.presentation.screen.vehicleedit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.segnities007.stylish_mycars.domain.model.Vehicle
import com.segnities007.stylish_mycars.domain.service.InspectionCalculator
import com.segnities007.stylish_mycars.domain.usecase.vehicle.DeleteVehicleUseCase
import com.segnities007.stylish_mycars.domain.usecase.vehicle.GetVehicleUseCase
import com.segnities007.stylish_mycars.domain.usecase.vehicle.InsertVehicleUseCase
import com.segnities007.stylish_mycars.domain.usecase.vehicle.UpdateVehicleUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class VehicleEditViewModel(
    private val vehicleId: Long?,
    private val getVehicleUseCase: GetVehicleUseCase,
    private val insertVehicleUseCase: InsertVehicleUseCase,
    private val updateVehicleUseCase: UpdateVehicleUseCase,
    private val deleteVehicleUseCase: DeleteVehicleUseCase,
    private val appContext: android.content.Context,
) : ViewModel() {
    private val _uiState = MutableStateFlow(VehicleEditUiState(vehicleId = vehicleId))
    val uiState: StateFlow<VehicleEditUiState> = _uiState.asStateFlow()

    private val _effects = Channel<VehicleEditEffect>(Channel.BUFFERED)
    val effects: Flow<VehicleEditEffect> = _effects.receiveAsFlow()

    init {
        if (vehicleId != null) {
            viewModelScope.launch {
                val vehicle = getVehicleUseCase(vehicleId).first() ?: return@launch
                _uiState.update {
                    it.copy(
                        category = vehicle.category,
                        maker = vehicle.maker,
                        name = vehicle.name,
                        grade = vehicle.grade,
                        year = vehicle.year?.toString() ?: "",
                        modelCode = vehicle.modelCode,
                        plateNumber = vehicle.plateNumber,
                        displacement = vehicle.displacement?.toString() ?: "",
                        weight = vehicle.weight?.toString() ?: "",
                        maxLoadKg = vehicle.maxLoadKg?.toString() ?: "",
                        color = vehicle.color,
                        firstRegistrationDate = vehicle.firstRegistrationDate,
                        jibaiExpiry = vehicle.jibaiExpiry,
                        insuranceExpiry = vehicle.insuranceExpiry,
                        insuranceCompany = vehicle.insuranceCompany,
                        insuranceRank = vehicle.insuranceRank?.toString() ?: "",
                        taxPaid = vehicle.taxPaid,
                        photoUri = vehicle.photoUri,
                        memo = vehicle.memo,
                        isEditing = true,
                    )
                }
            }
        }
    }

    fun accept(intent: VehicleEditIntent) {
        when (intent) {
            is VehicleEditIntent.CategoryChanged ->
                _uiState.update { it.copy(category = intent.value) }
            is VehicleEditIntent.MakerChanged ->
                _uiState.update { it.copy(maker = intent.value) }
            is VehicleEditIntent.NameChanged ->
                _uiState.update { it.copy(name = intent.value) }
            is VehicleEditIntent.GradeChanged ->
                _uiState.update { it.copy(grade = intent.value) }
            is VehicleEditIntent.YearChanged ->
                _uiState.update { it.copy(year = intent.value.filter { c -> c.isDigit() }.take(4)) }
            is VehicleEditIntent.ModelCodeChanged ->
                _uiState.update { it.copy(modelCode = intent.value) }
            is VehicleEditIntent.PlateNumberChanged ->
                _uiState.update { it.copy(plateNumber = intent.value) }
            is VehicleEditIntent.DisplacementChanged ->
                _uiState.update { it.copy(displacement = intent.value.filter { c -> c.isDigit() }) }
            is VehicleEditIntent.WeightChanged ->
                _uiState.update { it.copy(weight = intent.value.filter { c -> c.isDigit() }) }
            is VehicleEditIntent.MaxLoadKgChanged ->
                _uiState.update { it.copy(maxLoadKg = intent.value.filter { c -> c.isDigit() }) }
            is VehicleEditIntent.ColorChanged ->
                _uiState.update { it.copy(color = intent.value) }
            is VehicleEditIntent.FirstRegistrationDateChanged ->
                _uiState.update { it.copy(firstRegistrationDate = intent.value) }
            is VehicleEditIntent.JibaiExpiryChanged ->
                _uiState.update { it.copy(jibaiExpiry = intent.value) }
            is VehicleEditIntent.InsuranceExpiryChanged ->
                _uiState.update { it.copy(insuranceExpiry = intent.value) }
            is VehicleEditIntent.InsuranceCompanyChanged ->
                _uiState.update { it.copy(insuranceCompany = intent.value) }
            is VehicleEditIntent.InsuranceRankChanged ->
                _uiState.update { it.copy(insuranceRank = intent.value.filter { c -> c.isDigit() }.take(2)) }
            is VehicleEditIntent.TaxPaidChanged ->
                _uiState.update { it.copy(taxPaid = intent.value) }
            is VehicleEditIntent.PhotoUriChanged ->
                _uiState.update { it.copy(photoUri = intent.value) }
            is VehicleEditIntent.MemoChanged ->
                _uiState.update { it.copy(memo = intent.value) }
            is VehicleEditIntent.Save -> save()
            is VehicleEditIntent.RequestDelete ->
                _uiState.update { it.copy(showDeleteDialog = true) }
            is VehicleEditIntent.ConfirmDelete -> confirmDelete()
            is VehicleEditIntent.DismissDeleteDialog ->
                _uiState.update { it.copy(showDeleteDialog = false) }
            is VehicleEditIntent.NavigateBack ->
                _effects.trySend(VehicleEditEffect.NavigateBack)
        }
    }

    private fun save() {
        val state = _uiState.value
        if (!state.canSave) return
        _uiState.update { it.copy(isSaving = true) }

        viewModelScope.launch {
            val inspectionExpiry = state.firstRegistrationDate?.let {
                InspectionCalculator.calculateCurrentExpiry(state.category, it, state.displacement.toIntOrNull())
            }
            val vehicle = Vehicle(
                id = state.vehicleId ?: 0,
                category = state.category,
                maker = state.maker.trim(),
                name = state.name.trim(),
                grade = state.grade.trim(),
                year = state.year.toIntOrNull(),
                modelCode = state.modelCode.trim(),
                plateNumber = state.plateNumber.trim(),
                displacement = state.displacement.toIntOrNull(),
                weight = state.weight.toIntOrNull(),
                maxLoadKg = state.maxLoadKg.toIntOrNull(),
                color = state.color.trim(),
                firstRegistrationDate = state.firstRegistrationDate,
                inspectionExpiry = inspectionExpiry,
                jibaiExpiry = state.jibaiExpiry,
                insuranceExpiry = state.insuranceExpiry,
                insuranceCompany = state.insuranceCompany.trim(),
                insuranceRank = state.insuranceRank.toIntOrNull(),
                taxPaid = state.taxPaid,
                photoUri = state.photoUri,
                memo = state.memo.trim(),
            )
            if (state.isEditing) {
                updateVehicleUseCase(vehicle)
            } else {
                insertVehicleUseCase(vehicle)
            }
            com.segnities007.stylish_mycars.widget.WidgetUpdater.update(appContext)
            _effects.send(VehicleEditEffect.NavigateBack)
        }
    }

    private fun confirmDelete() {
        val state = _uiState.value
        val id = state.vehicleId ?: return
        viewModelScope.launch {
            val vehicle = getVehicleUseCase(id).first() ?: return@launch
            deleteVehicleUseCase(vehicle)
            com.segnities007.stylish_mycars.widget.WidgetUpdater.update(appContext)
            _effects.send(VehicleEditEffect.NavigateBack)
        }
    }
}

sealed interface VehicleEditEffect {
    data object NavigateBack : VehicleEditEffect
}
