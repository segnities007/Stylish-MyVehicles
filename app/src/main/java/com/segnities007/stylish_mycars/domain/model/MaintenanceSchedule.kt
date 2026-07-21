package com.segnities007.stylish_mycars.domain.model

import java.time.LocalDate

data class MaintenanceSchedule(
    val id: Long = 0,
    val vehicleId: Long,
    val category: MaintenanceCategory,
    val intervalKm: Int? = null,
    val intervalMonths: Int? = null,
    val lastDoneDate: LocalDate? = null,
    val lastDoneOdometer: Int? = null,
) {
    companion object {
        /** 後方互換: 乗用車前提の初期値。 */
        fun defaults(vehicleId: Long): List<MaintenanceSchedule> =
            defaults(vehicleId, VehicleCategory.CAR)

        /** カテゴリ別のメンテナンス目安初期値。 */
        fun defaults(vehicleId: Long, category: VehicleCategory): List<MaintenanceSchedule> =
            when (category) {
                VehicleCategory.CAR, VehicleCategory.KEI_CAR, VehicleCategory.OTHER -> carDefaults(
                    vehicleId
                )

                VehicleCategory.MOTORCYCLE -> motorcycleDefaults(vehicleId)
                VehicleCategory.BICYCLE -> bicycleDefaults(vehicleId)
                VehicleCategory.TRUCK -> truckDefaults(vehicleId)
            }

        private fun carDefaults(vehicleId: Long) = listOf(
            MaintenanceSchedule(
                vehicleId = vehicleId,
                category = MaintenanceCategory.OIL,
                intervalKm = 5000,
                intervalMonths = 6
            ),
            MaintenanceSchedule(
                vehicleId = vehicleId,
                category = MaintenanceCategory.OIL_FILTER,
                intervalKm = 10000,
                intervalMonths = 12
            ),
            MaintenanceSchedule(
                vehicleId = vehicleId,
                category = MaintenanceCategory.TIRE,
                intervalKm = 30000,
                intervalMonths = 36
            ),
            MaintenanceSchedule(
                vehicleId = vehicleId,
                category = MaintenanceCategory.BATTERY,
                intervalMonths = 36
            ),
            MaintenanceSchedule(
                vehicleId = vehicleId,
                category = MaintenanceCategory.BRAKE,
                intervalKm = 30000
            ),
            MaintenanceSchedule(
                vehicleId = vehicleId,
                category = MaintenanceCategory.WIPER,
                intervalMonths = 12
            ),
            MaintenanceSchedule(
                vehicleId = vehicleId,
                category = MaintenanceCategory.AIR_FILTER,
                intervalKm = 10000,
                intervalMonths = 12
            ),
            MaintenanceSchedule(
                vehicleId = vehicleId,
                category = MaintenanceCategory.COOLANT,
                intervalMonths = 24
            ),
            MaintenanceSchedule(
                vehicleId = vehicleId,
                category = MaintenanceCategory.INSPECTION_12,
                intervalMonths = 12
            ),
        )

        private fun motorcycleDefaults(vehicleId: Long) = listOf(
            MaintenanceSchedule(
                vehicleId = vehicleId,
                category = MaintenanceCategory.OIL,
                intervalKm = 3000,
                intervalMonths = 6
            ),
            MaintenanceSchedule(
                vehicleId = vehicleId,
                category = MaintenanceCategory.OIL_FILTER,
                intervalKm = 6000,
                intervalMonths = 12
            ),
            MaintenanceSchedule(
                vehicleId = vehicleId,
                category = MaintenanceCategory.CHAIN,
                intervalKm = 1000,
                intervalMonths = 1
            ),
            MaintenanceSchedule(
                vehicleId = vehicleId,
                category = MaintenanceCategory.TIRE,
                intervalKm = 10000,
                intervalMonths = 24
            ),
            MaintenanceSchedule(
                vehicleId = vehicleId,
                category = MaintenanceCategory.BATTERY,
                intervalMonths = 24
            ),
            MaintenanceSchedule(
                vehicleId = vehicleId,
                category = MaintenanceCategory.BRAKE,
                intervalKm = 10000
            ),
            MaintenanceSchedule(
                vehicleId = vehicleId,
                category = MaintenanceCategory.SPARK_PLUG,
                intervalKm = 10000
            ),
            MaintenanceSchedule(
                vehicleId = vehicleId,
                category = MaintenanceCategory.COOLANT,
                intervalMonths = 24
            ),
        )

        private fun bicycleDefaults(vehicleId: Long) = listOf(
            MaintenanceSchedule(
                vehicleId = vehicleId,
                category = MaintenanceCategory.TIRE,
                intervalMonths = 12
            ),
            MaintenanceSchedule(
                vehicleId = vehicleId,
                category = MaintenanceCategory.BRAKE,
                intervalMonths = 6
            ),
            MaintenanceSchedule(
                vehicleId = vehicleId,
                category = MaintenanceCategory.CHAIN,
                intervalMonths = 1
            ),
            MaintenanceSchedule(
                vehicleId = vehicleId,
                category = MaintenanceCategory.GEAR,
                intervalMonths = 6
            ),
            MaintenanceSchedule(
                vehicleId = vehicleId,
                category = MaintenanceCategory.GENERAL_INSPECTION,
                intervalMonths = 12
            ),
        )

        private fun truckDefaults(vehicleId: Long) = listOf(
            MaintenanceSchedule(
                vehicleId = vehicleId,
                category = MaintenanceCategory.OIL,
                intervalKm = 10000,
                intervalMonths = 6
            ),
            MaintenanceSchedule(
                vehicleId = vehicleId,
                category = MaintenanceCategory.OIL_FILTER,
                intervalKm = 20000,
                intervalMonths = 12
            ),
            MaintenanceSchedule(
                vehicleId = vehicleId,
                category = MaintenanceCategory.TIRE,
                intervalKm = 40000
            ),
            MaintenanceSchedule(
                vehicleId = vehicleId,
                category = MaintenanceCategory.BATTERY,
                intervalMonths = 24
            ),
            MaintenanceSchedule(
                vehicleId = vehicleId,
                category = MaintenanceCategory.BRAKE,
                intervalKm = 30000
            ),
            MaintenanceSchedule(
                vehicleId = vehicleId,
                category = MaintenanceCategory.COOLANT,
                intervalMonths = 24
            ),
        )
    }
}
