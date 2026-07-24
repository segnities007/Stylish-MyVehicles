package com.segnities007.stylish_myvehicles.presentation.screen.recordslist

sealed interface RecordsListEffect {
    data object NavigateBack : RecordsListEffect
    data class OpenFuel(val vehicleId: Long) : RecordsListEffect
    data class OpenMaintenance(val vehicleId: Long) : RecordsListEffect
    data class OpenCost(val vehicleId: Long) : RecordsListEffect
    data class OpenTrip(val vehicleId: Long) : RecordsListEffect
}
