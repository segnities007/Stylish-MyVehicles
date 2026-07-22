package com.segnities007.stylish_myvehicles.presentation.navigation

import androidx.navigation3.runtime.NavKey
import com.segnities007.stylish_myvehicles.presentation.screen.records.RecordTopic

internal sealed interface AppDestination : NavKey
internal data object OnboardingDestination : AppDestination
internal data object VehiclePagerDestination : AppDestination
internal data class VehicleEditDestination(val vehicleId: Long?) : AppDestination
internal data class RecordsDestination(val vehicleId: Long, val topic: RecordTopic) : AppDestination
internal data class VehicleDetailDestination(val vehicleId: Long) : AppDestination
internal data class FuelRecordDestination(val vehicleId: Long, val openAdd: Boolean = false) : AppDestination
internal data class MaintenanceRecordDestination(val vehicleId: Long, val openAdd: Boolean = false) : AppDestination
internal data class CostListDestination(val vehicleId: Long, val openAdd: Boolean = false) : AppDestination
internal data object SettingsDestination : AppDestination
internal data object LicensesDestination : AppDestination
internal data object NotificationDestination : AppDestination
