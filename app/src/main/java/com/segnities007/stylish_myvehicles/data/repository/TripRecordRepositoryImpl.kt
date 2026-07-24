package com.segnities007.stylish_myvehicles.data.repository

import com.segnities007.stylish_myvehicles.data.local.dao.TripRecordDao
import com.segnities007.stylish_myvehicles.data.mapper.toDomain
import com.segnities007.stylish_myvehicles.data.mapper.toEntity
import com.segnities007.stylish_myvehicles.domain.model.TripRecord
import com.segnities007.stylish_myvehicles.domain.repository.TripRecordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TripRecordRepositoryImpl(
    private val dao: TripRecordDao,
) : TripRecordRepository {
    override fun getByVehicleId(vehicleId: Long): Flow<List<TripRecord>> =
        dao.getByVehicleId(vehicleId).map { records -> records.map { it.toDomain() } }

    override suspend fun insert(record: TripRecord): Long = dao.insert(record.toEntity())
    override suspend fun update(record: TripRecord) = dao.update(record.toEntity())
    override suspend fun delete(record: TripRecord) = dao.delete(record.toEntity())
}
