package com.segnities007.stylish_mycars.domain.usecase.fuel

import com.segnities007.stylish_mycars.domain.model.FuelRecord
import com.segnities007.stylish_mycars.domain.repository.FuelRecordRepository

class DeleteFuelRecordUseCase(private val repository: FuelRecordRepository) {
    suspend operator fun invoke(record: FuelRecord) = repository.delete(record)
}
