package com.segnities007.stylish_myvehicles.domain.repository

import com.segnities007.stylish_myvehicles.domain.model.FuelRecord
import kotlinx.coroutines.flow.Flow

interface FuelRecordRepository {
    fun getByVehicleId(vehicleId: Long): Flow<List<FuelRecord>>
    suspend fun getLatest(vehicleId: Long): FuelRecord?
    suspend fun insert(record: FuelRecord): Long
    suspend fun update(record: FuelRecord)
    suspend fun delete(record: FuelRecord)
    suspend fun deleteById(id: Long)
}
