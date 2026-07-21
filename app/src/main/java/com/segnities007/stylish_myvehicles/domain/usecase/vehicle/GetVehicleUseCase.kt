package com.segnities007.stylish_myvehicles.domain.usecase.vehicle

import com.segnities007.stylish_myvehicles.domain.model.Vehicle
import com.segnities007.stylish_myvehicles.domain.repository.VehicleRepository
import kotlinx.coroutines.flow.Flow

class GetVehicleUseCase(private val repository: VehicleRepository) {
    operator fun invoke(id: Long): Flow<Vehicle?> = repository.getById(id)
}
