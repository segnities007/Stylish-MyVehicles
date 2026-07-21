package com.segnities007.stylish_mycars.domain.repository

import com.segnities007.stylish_mycars.domain.model.MaintenanceSchedule
import com.segnities007.stylish_mycars.domain.model.VehicleCategory
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

interface MaintenanceScheduleRepository {
    fun getByVehicleId(vehicleId: Long): Flow<List<MaintenanceSchedule>>
    suspend fun getAll(): List<MaintenanceSchedule>
    suspend fun insertDefaults(vehicleId: Long, category: VehicleCategory = VehicleCategory.CAR)
    suspend fun update(schedule: MaintenanceSchedule)
    suspend fun updateLastDone(vehicleId: Long, category: String, date: LocalDate, odometer: Int?)
}
