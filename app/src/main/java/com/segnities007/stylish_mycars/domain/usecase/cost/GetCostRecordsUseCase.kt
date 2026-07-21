package com.segnities007.stylish_mycars.domain.usecase.cost

import com.segnities007.stylish_mycars.domain.model.CostRecord
import com.segnities007.stylish_mycars.domain.repository.CostRecordRepository
import kotlinx.coroutines.flow.Flow

class GetCostRecordsUseCase(private val repository: CostRecordRepository) {
    operator fun invoke(vehicleId: Long): Flow<List<CostRecord>> =
        repository.getByVehicleId(vehicleId)
}
