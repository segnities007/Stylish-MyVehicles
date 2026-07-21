package com.segnities007.stylish_mycars.domain.repository

import com.segnities007.stylish_mycars.domain.model.MaintenanceRecord
import kotlinx.coroutines.flow.Flow

interface MaintenanceRecordRepository {
    fun getByVehicleId(vehicleId: Long): Flow<List<MaintenanceRecord>>
    suspend fun insert(record: MaintenanceRecord): Long
    suspend fun update(record: MaintenanceRecord)
    suspend fun delete(record: MaintenanceRecord)
}
