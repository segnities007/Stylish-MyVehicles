package com.segnities007.stylish_myvehicles.domain.repository

import com.segnities007.stylish_myvehicles.domain.model.TripRecord
import kotlinx.coroutines.flow.Flow

interface TripRecordRepository {
    fun getByVehicleId(vehicleId: Long): Flow<List<TripRecord>>
    suspend fun insert(record: TripRecord): Long
    suspend fun update(record: TripRecord)
    suspend fun delete(record: TripRecord)
}
