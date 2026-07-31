package com.segnities007.stylish_myvehicles.presentation.screen.vehicledetail

import com.segnities007.stylish_myvehicles.domain.usecase.ExportDocument

sealed interface VehicleDetailEffect {
    data object NavigateBack : VehicleDetailEffect
    data class NavigateToEdit(val vehicleId: Long) : VehicleDetailEffect
    data class NavigateToCost(val vehicleId: Long) : VehicleDetailEffect
    data class SaveDocument(val document: ExportDocument) :
        VehicleDetailEffect
}
