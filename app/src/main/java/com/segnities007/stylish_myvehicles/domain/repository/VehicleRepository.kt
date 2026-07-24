package com.segnities007.stylish_myvehicles.domain.repository

import com.segnities007.stylish_myvehicles.domain.model.Vehicle
import kotlinx.coroutines.flow.Flow

interface VehicleRepository {
    fun getAll(): Flow<List<Vehicle>>
    fun getById(id: Long): Flow<Vehicle?>
    suspend fun insert(vehicle: Vehicle): Long
    suspend fun update(vehicle: Vehicle)
    suspend fun delete(vehicle: Vehicle)
    suspend fun deleteById(id: Long)
}
