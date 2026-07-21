package com.segnities007.stylish_mycars.domain.usecase.cost

import com.segnities007.stylish_mycars.domain.model.CostRecord
import com.segnities007.stylish_mycars.domain.repository.CostRecordRepository

class UpdateCostRecordUseCase(
    private val repository: CostRecordRepository,
) {
    suspend operator fun invoke(record: CostRecord) =
        repository.update(record)
}
