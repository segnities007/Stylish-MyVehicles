package com.segnities007.stylish_mycars.presentation.screen.vehiclelist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.segnities007.stylish_mycars.domain.usecase.vehicle.GetVehiclesUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class VehicleListViewModel(
    private val getVehiclesUseCase: GetVehiclesUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(VehicleListUiState())
    val uiState: StateFlow<VehicleListUiState> = _uiState.asStateFlow()

    private val _effects = Channel<VehicleListEffect>(Channel.BUFFERED)
    val effects: Flow<VehicleListEffect> = _effects.receiveAsFlow()

    init {
        viewModelScope.launch {
            getVehiclesUseCase().collect { vehicles ->
                _uiState.update { it.copy(vehicles = vehicles, isLoading = false) }
            }
        }
    }

    fun accept(intent: VehicleListIntent) {
        when (intent) {
            is VehicleListIntent.AddVehicle ->
                _effects.trySend(VehicleListEffect.NavigateToEdit(null))
            is VehicleListIntent.SelectVehicle ->
                _effects.trySend(VehicleListEffect.NavigateToDetail(intent.vehicleId))
            is VehicleListIntent.SearchQueryChanged ->
                _uiState.update { it.copy(searchQuery = intent.query) }
        }
    }
}

sealed interface VehicleListEffect {
    data class NavigateToDetail(val vehicleId: Long) : VehicleListEffect
    data class NavigateToEdit(val vehicleId: Long?) : VehicleListEffect
}
