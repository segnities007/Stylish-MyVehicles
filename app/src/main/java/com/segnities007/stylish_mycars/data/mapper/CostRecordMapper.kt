package com.segnities007.stylish_mycars.data.mapper

import com.segnities007.stylish_mycars.data.local.entity.CostRecordEntity
import com.segnities007.stylish_mycars.domain.model.CostCategory
import com.segnities007.stylish_mycars.domain.model.CostRecord

fun CostRecordEntity.toDomain(): CostRecord = CostRecord(
    id = id,
    vehicleId = vehicleId,
    date = date,
    category = CostCategory.valueOf(category),
    title = title,
    amount = amount,
    memo = memo,
)

fun CostRecord.toEntity(): CostRecordEntity = CostRecordEntity(
    id = id,
    vehicleId = vehicleId,
    date = date,
    category = category.name,
    title = title,
    amount = amount,
    memo = memo,
)
