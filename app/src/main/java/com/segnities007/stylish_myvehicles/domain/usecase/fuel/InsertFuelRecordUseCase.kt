package com.segnities007.stylish_myvehicles.domain.usecase.fuel

import com.segnities007.stylish_myvehicles.domain.model.CostCategory
import com.segnities007.stylish_myvehicles.domain.model.CostRecord
import com.segnities007.stylish_myvehicles.domain.model.FuelRecord
import com.segnities007.stylish_myvehicles.domain.repository.CostRecordRepository
import com.segnities007.stylish_myvehicles.domain.repository.FuelRecordRepository

class InsertFuelRecordUseCase(
    private val fuelRepository: FuelRecordRepository,
    private val costRepository: CostRecordRepository,
) {
    suspend operator fun invoke(record: FuelRecord): Long {
        val id = fuelRepository.insert(record)
        costRepository.insert(
            CostRecord(
                vehicleId = record.vehicleId,
                date = record.date,
                category = CostCategory.FUEL,
                title = "給油 ${record.volume}L",
                amount = record.amount,
            ),
        )
        return id
    }
}
