package com.segnities007.stylish_mycars.domain.usecase.maintenance

import com.segnities007.stylish_mycars.domain.model.MaintenanceRecord
import com.segnities007.stylish_mycars.domain.repository.MaintenanceRecordRepository
import kotlinx.coroutines.flow.Flow

class GetMaintenanceRecordsUseCase(private val repository: MaintenanceRecordRepository) {
    operator fun invoke(vehicleId: Long): Flow<List<MaintenanceRecord>> =
        repository.getByVehicleId(vehicleId)
}
