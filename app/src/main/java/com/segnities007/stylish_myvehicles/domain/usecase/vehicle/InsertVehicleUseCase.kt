package com.segnities007.stylish_myvehicles.domain.usecase.vehicle

import com.segnities007.stylish_myvehicles.domain.model.Vehicle
import com.segnities007.stylish_myvehicles.domain.repository.MaintenanceScheduleRepository
import com.segnities007.stylish_myvehicles.domain.repository.VehicleRepository

class InsertVehicleUseCase(
    private val repository: VehicleRepository,
    private val scheduleRepository: MaintenanceScheduleRepository,
) {
    suspend operator fun invoke(vehicle: Vehicle): Long {
        val id = repository.insert(vehicle)
        scheduleRepository.insertDefaults(id, vehicle.category)
        return id
    }
}
