package com.segnities007.stylish_myvehicles.domain.usecase.fuel

import com.segnities007.stylish_myvehicles.domain.model.FuelRecord
import com.segnities007.stylish_myvehicles.domain.repository.FuelRecordRepository

class UpdateFuelRecordUseCase(
    private val fuelRepository: FuelRecordRepository,
) {
    suspend operator fun invoke(record: FuelRecord) {
        fuelRepository.update(record)
    }
}
