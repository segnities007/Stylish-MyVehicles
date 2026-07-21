package com.segnities007.stylish_myvehicles.domain.usecase.maintenance

import com.segnities007.stylish_myvehicles.domain.model.MaintenanceRecord
import com.segnities007.stylish_myvehicles.domain.repository.MaintenanceRecordRepository
import kotlinx.coroutines.flow.Flow

class GetMaintenanceRecordsUseCase(private val repository: MaintenanceRecordRepository) {
    operator fun invoke(vehicleId: Long): Flow<List<MaintenanceRecord>> =
        repository.getByVehicleId(vehicleId)
}
