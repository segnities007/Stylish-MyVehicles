package com.segnities007.stylish_mycars.domain.usecase.maintenance

import com.segnities007.stylish_mycars.domain.model.MaintenanceRecord
import com.segnities007.stylish_mycars.domain.repository.MaintenanceRecordRepository

class UpdateMaintenanceRecordUseCase(
    private val maintenanceRepository: MaintenanceRecordRepository,
) {
    suspend operator fun invoke(record: MaintenanceRecord) {
        maintenanceRepository.update(record)
    }
}
