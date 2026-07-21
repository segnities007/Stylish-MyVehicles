package com.segnities007.stylish_mycars.domain.usecase.vehicle

import com.segnities007.stylish_mycars.domain.model.Vehicle
import com.segnities007.stylish_mycars.domain.repository.VehicleRepository

class UpdateVehicleUseCase(private val repository: VehicleRepository) {
    suspend operator fun invoke(vehicle: Vehicle) = repository.update(vehicle)
}
