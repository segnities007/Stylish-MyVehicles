package com.segnities007.stylish_myvehicles.domain.usecase.maintenance

import com.segnities007.stylish_myvehicles.domain.model.MaintenanceSchedule
import com.segnities007.stylish_myvehicles.domain.repository.MaintenanceScheduleRepository

class UpdateMaintenanceScheduleUseCase(
    private val repository: MaintenanceScheduleRepository,
) {
    suspend operator fun invoke(schedule: MaintenanceSchedule) =
        repository.update(schedule)
}
