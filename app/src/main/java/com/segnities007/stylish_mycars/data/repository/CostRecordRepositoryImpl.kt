package com.segnities007.stylish_mycars.data.repository

import com.segnities007.stylish_mycars.data.local.dao.CostRecordDao
import com.segnities007.stylish_mycars.data.mapper.toDomain
import com.segnities007.stylish_mycars.data.mapper.toEntity
import com.segnities007.stylish_mycars.domain.model.CostRecord
import com.segnities007.stylish_mycars.domain.repository.CostRecordRepository
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CostRecordRepositoryImpl(
    private val dao: CostRecordDao,
) : CostRecordRepository {
    override fun getByVehicleId(vehicleId: Long): Flow<List<CostRecord>> =
        dao.getByVehicleId(vehicleId).map { entities -> entities.map { it.toDomain() } }

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
