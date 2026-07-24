package com.segnities007.stylish_myvehicles.data.repository

import com.segnities007.stylish_myvehicles.data.local.dao.CostRecordDao
import com.segnities007.stylish_myvehicles.data.mapper.toDomain
import com.segnities007.stylish_myvehicles.data.mapper.toEntity
import com.segnities007.stylish_myvehicles.domain.model.CostRecord
import com.segnities007.stylish_myvehicles.domain.repository.CostRecordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class CostRecordRepositoryImpl(
    private val dao: CostRecordDao,
) : CostRecordRepository {
    override fun getByVehicleId(vehicleId: Long): Flow<List<CostRecord>> =
        dao.getByVehicleId(vehicleId)
            .map { entities -> entities.map { it.toDomain() } }

    override fun getByVehicleIdAndDateRange(
        vehicleId: Long,
        start: LocalDate,
        end: LocalDate,
    ): Flow<List<CostRecord>> =
        dao.getByVehicleIdAndDateRange(vehicleId, start, end)
            .map { entities -> entities.map { it.toDomain() } }

    override suspend fun insert(record: CostRecord): Long =
        dao.insert(record.toEntity())

    override suspend fun update(record: CostRecord) =
        dao.update(record.toEntity())

    override suspend fun delete(record: CostRecord) =
        dao.delete(record.toEntity())
}
