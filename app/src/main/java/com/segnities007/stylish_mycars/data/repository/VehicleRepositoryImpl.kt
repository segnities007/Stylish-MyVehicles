package com.segnities007.stylish_mycars.data.repository

import com.segnities007.stylish_mycars.data.local.dao.VehicleDao
import com.segnities007.stylish_mycars.data.mapper.toDomain
import com.segnities007.stylish_mycars.data.mapper.toEntity
import com.segnities007.stylish_mycars.domain.model.Vehicle
import com.segnities007.stylish_mycars.domain.repository.VehicleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class VehicleRepositoryImpl(
    private val dao: VehicleDao,
) : VehicleRepository {
    override fun getAll(): Flow<List<Vehicle>> =
        dao.getAll()
            .map { entities -> entities.map { it.toDomain() } }

    override fun getById(id: Long): Flow<Vehicle?> =
        dao.getById(id)
            .map { it?.toDomain() }

    override suspend fun insert(vehicle: Vehicle): Long =
        dao.insert(vehicle.toEntity())

    override suspend fun update(vehicle: Vehicle) =
        dao.update(vehicle.toEntity())

    override suspend fun delete(vehicle: Vehicle) =
        dao.delete(vehicle.toEntity())
}
