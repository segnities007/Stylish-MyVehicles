package com.segnities007.stylish_mycars.domain.usecase.maintenance

import com.segnities007.stylish_mycars.domain.model.MaintenanceSchedule
import com.segnities007.stylish_mycars.domain.repository.MaintenanceScheduleRepository
import kotlinx.coroutines.flow.Flow

class GetMaintenanceSchedulesUseCase(
    private val repository: MaintenanceScheduleRepository,
) {
    operator fun invoke(vehicleId: Long): Flow<List<MaintenanceSchedule>> =
        repository.getByVehicleId(vehicleId)
}
