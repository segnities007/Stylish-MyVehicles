package com.segnities007.stylish_myvehicles.domain.usecase.cost

import com.segnities007.stylish_myvehicles.domain.model.CostRecord
import com.segnities007.stylish_myvehicles.domain.repository.CostRecordRepository

class UpdateCostRecordUseCase(
    private val repository: CostRecordRepository,
) {
    suspend operator fun invoke(record: CostRecord) =
        repository.update(record)
}
