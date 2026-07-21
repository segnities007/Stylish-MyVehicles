package com.segnities007.stylish_myvehicles.domain.usecase.vehicle

import com.segnities007.stylish_myvehicles.domain.model.Vehicle
import com.segnities007.stylish_myvehicles.domain.repository.VehicleRepository
import kotlinx.coroutines.flow.Flow

class GetVehiclesUseCase(private val repository: VehicleRepository) {
    operator fun invoke(): Flow<List<Vehicle>> = repository.getAll()
}
