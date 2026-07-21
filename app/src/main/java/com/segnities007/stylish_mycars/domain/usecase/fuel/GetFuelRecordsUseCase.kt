package com.segnities007.stylish_mycars.domain.usecase.fuel

import com.segnities007.stylish_mycars.domain.model.FuelRecord
import com.segnities007.stylish_mycars.domain.repository.FuelRecordRepository
import kotlinx.coroutines.flow.Flow

class GetFuelRecordsUseCase(private val repository: FuelRecordRepository) {
    operator fun invoke(vehicleId: Long): Flow<List<FuelRecord>> =
        repository.getByVehicleId(vehicleId)
}
