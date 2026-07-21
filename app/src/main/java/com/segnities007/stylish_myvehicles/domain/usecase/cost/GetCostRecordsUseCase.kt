package com.segnities007.stylish_myvehicles.domain.usecase.cost

import com.segnities007.stylish_myvehicles.domain.model.CostRecord
import com.segnities007.stylish_myvehicles.domain.repository.CostRecordRepository
import kotlinx.coroutines.flow.Flow

class GetCostRecordsUseCase(private val repository: CostRecordRepository) {
    operator fun invoke(vehicleId: Long): Flow<List<CostRecord>> =
        repository.getByVehicleId(vehicleId)
}
