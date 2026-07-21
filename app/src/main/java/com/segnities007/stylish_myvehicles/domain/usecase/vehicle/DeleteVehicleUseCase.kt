package com.segnities007.stylish_myvehicles.domain.usecase.vehicle

import com.segnities007.stylish_myvehicles.domain.model.Vehicle
import com.segnities007.stylish_myvehicles.domain.repository.VehicleRepository

class DeleteVehicleUseCase(private val repository: VehicleRepository) {
    suspend operator fun invoke(vehicle: Vehicle) = repository.delete(vehicle)
}
