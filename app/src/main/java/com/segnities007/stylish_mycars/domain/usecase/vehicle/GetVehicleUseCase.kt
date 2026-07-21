package com.segnities007.stylish_mycars.domain.usecase.vehicle

import com.segnities007.stylish_mycars.domain.model.Vehicle
import com.segnities007.stylish_mycars.domain.repository.VehicleRepository
import kotlinx.coroutines.flow.Flow

class GetVehicleUseCase(private val repository: VehicleRepository) {
    operator fun invoke(id: Long): Flow<Vehicle?> = repository.getById(id)
}
