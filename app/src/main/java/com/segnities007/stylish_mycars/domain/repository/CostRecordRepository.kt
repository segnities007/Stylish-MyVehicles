package com.segnities007.stylish_mycars.domain.repository

import com.segnities007.stylish_mycars.domain.model.CostRecord
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

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
