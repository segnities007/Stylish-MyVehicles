package com.segnities007.stylish_mycars.domain.usecase.maintenance

import com.segnities007.stylish_mycars.domain.model.MaintenanceSchedule
import com.segnities007.stylish_mycars.domain.repository.MaintenanceScheduleRepository

class UpdateMaintenanceScheduleUseCase(
    private val repository: MaintenanceScheduleRepository,
) {
    suspend operator fun invoke(schedule: MaintenanceSchedule) =
        repository.update(schedule)
}
