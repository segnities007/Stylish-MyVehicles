package com.segnities007.stylish_mycars.domain.usecase.maintenance

import com.segnities007.stylish_mycars.domain.model.MaintenanceRecord
import com.segnities007.stylish_mycars.domain.repository.MaintenanceRecordRepository

class DeleteMaintenanceRecordUseCase(private val repository: MaintenanceRecordRepository) {
    suspend operator fun invoke(record: MaintenanceRecord) = repository.delete(record)
}
