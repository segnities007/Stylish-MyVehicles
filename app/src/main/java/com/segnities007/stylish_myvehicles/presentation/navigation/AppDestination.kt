package com.segnities007.stylish_myvehicles.presentation.navigation

import androidx.navigation3.runtime.NavKey

internal sealed interface AppDestination : NavKey
internal data object OnboardingDestination : AppDestination
internal data object VehiclePagerDestination : AppDestination
/**
 * Navigation 3ではNavDisplay配下のViewModelStoreが画面ごとに分かれないため、
 * 同じ登録・編集画面を開き直した際に古い入力状態を再利用しないようセッションを識別する。
 */
internal data class VehicleEditDestination(
    val vehicleId: Long?,
    val sessionId: Long = nextVehicleEditSessionId(),
) : AppDestination

private var vehicleEditSessionSequence = 0L

@Synchronized
private fun nextVehicleEditSessionId(): Long = ++vehicleEditSessionSequence
internal data class FuelRecordsDestination(val vehicleId: Long, val openAdd: Boolean = false) :
    AppDestination

internal data class MaintenanceRecordsDestination(val vehicleId: Long, val openAdd: Boolean = false) :
    AppDestination

internal data class CostRecordsDestination(val vehicleId: Long, val openAdd: Boolean = false) :
    AppDestination

internal data class TripRecordsDestination(val vehicleId: Long) : AppDestination

internal data class VehicleDetailDestination(val vehicleId: Long) : AppDestination
internal data object RecordsListDestination : AppDestination
internal data object SettingsDestination : AppDestination
internal data object LicensesDestination : AppDestination
internal data object NotificationDestination : AppDestination
