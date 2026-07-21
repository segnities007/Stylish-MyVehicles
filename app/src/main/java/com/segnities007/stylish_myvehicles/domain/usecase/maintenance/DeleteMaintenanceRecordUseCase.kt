package com.segnities007.stylish_myvehicles.domain.usecase.maintenance

import com.segnities007.stylish_myvehicles.domain.model.MaintenanceRecord
import com.segnities007.stylish_myvehicles.domain.repository.MaintenanceRecordRepository

class DeleteMaintenanceRecordUseCase(private val repository: MaintenanceRecordRepository) {
    suspend operator fun invoke(record: MaintenanceRecord) = repository.delete(record)
}
