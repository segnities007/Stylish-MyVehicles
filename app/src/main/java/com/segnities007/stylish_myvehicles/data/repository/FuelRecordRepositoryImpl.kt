package com.segnities007.stylish_myvehicles.data.repository

import com.segnities007.stylish_myvehicles.data.local.dao.FuelRecordDao
import com.segnities007.stylish_myvehicles.data.mapper.toDomain
import com.segnities007.stylish_myvehicles.data.mapper.toEntity
import com.segnities007.stylish_myvehicles.domain.model.FuelRecord
import com.segnities007.stylish_myvehicles.domain.repository.FuelRecordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FuelRecordRepositoryImpl(
    private val dao: FuelRecordDao,
) : FuelRecordRepository {
    override fun getByVehicleId(vehicleId: Long): Flow<List<FuelRecord>> =
        dao.getByVehicleId(vehicleId)
            .map { entities -> entities.map { it.toDomain() } }

    override suspend fun getLatest(vehicleId: Long): FuelRecord? =
        dao.getLatest(vehicleId)
            ?.toDomain()

    override suspend fun insert(record: FuelRecord): Long =
        dao.insert(record.toEntity())

    override suspend fun update(record: FuelRecord) =
        dao.update(record.toEntity())

    override suspend fun delete(record: FuelRecord) =
        dao.delete(record.toEntity())

    override suspend fun deleteById(id: Long) =
        dao.deleteById(id)
}
