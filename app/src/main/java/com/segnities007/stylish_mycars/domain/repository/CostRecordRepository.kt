package com.segnities007.stylish_mycars.domain.repository

import com.segnities007.stylish_mycars.domain.model.CostRecord
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

interface CostRecordRepository {
    fun getByVehicleId(vehicleId: Long): Flow<List<CostRecord>>
    fun getByVehicleIdAndDateRange(
        vehicleId: Long,
        start: LocalDate,
        end: LocalDate,
    ): Flow<List<CostRecord>>
    suspend fun insert(record: CostRecord): Long
    suspend fun update(record: CostRecord)
    suspend fun delete(record: CostRecord)
}
