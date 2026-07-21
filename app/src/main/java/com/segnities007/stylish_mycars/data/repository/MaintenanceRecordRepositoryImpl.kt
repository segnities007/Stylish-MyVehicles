package com.segnities007.stylish_mycars.data.repository

import com.segnities007.stylish_mycars.data.local.dao.MaintenanceRecordDao
import com.segnities007.stylish_mycars.data.mapper.toDomain
import com.segnities007.stylish_mycars.data.mapper.toEntity
import com.segnities007.stylish_mycars.domain.model.MaintenanceRecord
import com.segnities007.stylish_mycars.domain.repository.MaintenanceRecordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MaintenanceRecordRepositoryImpl(
    private val dao: MaintenanceRecordDao,
) : MaintenanceRecordRepository {
    override fun getByVehicleId(vehicleId: Long): Flow<List<MaintenanceRecord>> =
        dao.getByVehicleId(vehicleId)
            .map { entities -> entities.map { it.toDomain() } }

    override suspend fun insert(record: MaintenanceRecord): Long =
        dao.insert(record.toEntity())

    override suspend fun update(record: MaintenanceRecord) =
        dao.update(record.toEntity())

    override suspend fun delete(record: MaintenanceRecord) =
        dao.delete(record.toEntity())
}
