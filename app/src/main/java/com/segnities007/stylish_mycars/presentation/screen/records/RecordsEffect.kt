package com.segnities007.stylish_mycars.presentation.screen.records

sealed interface RecordsEffect {
    data object NavigateBack : RecordsEffect
    data class OpenFuel(val vehicleId: Long, val recordId: Long? = null) : RecordsEffect
    data class OpenMaintenance(val vehicleId: Long, val recordId: Long? = null) : RecordsEffect
    data class OpenCost(val vehicleId: Long, val recordId: Long? = null) : RecordsEffect
}
