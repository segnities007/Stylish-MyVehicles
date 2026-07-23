package com.segnities007.stylish_myvehicles.presentation.navigation

import androidx.navigation3.runtime.NavKey

internal sealed interface AppDestination : NavKey
internal data object OnboardingDestination : AppDestination
internal data object VehiclePagerDestination : AppDestination
internal data class VehicleEditDestination(val vehicleId: Long?) : AppDestination
internal data class FuelRecordsDestination(val vehicleId: Long, val openAdd: Boolean = false) :
    AppDestination

internal data class MaintenanceRecordsDestination(val vehicleId: Long, val openAdd: Boolean = false) :
    AppDestination

internal data class CostRecordsDestination(val vehicleId: Long, val openAdd: Boolean = false) :
    AppDestination

internal data class VehicleDetailDestination(val vehicleId: Long) : AppDestination
internal data object SettingsDestination : AppDestination
internal data object LicensesDestination : AppDestination
internal data object NotificationDestination : AppDestination
