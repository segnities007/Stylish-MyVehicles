package com.segnities007.stylish_mycars.data.mapper

import com.segnities007.stylish_mycars.data.local.entity.FuelRecordEntity
import com.segnities007.stylish_mycars.domain.model.FuelRecord

fun FuelRecordEntity.toDomain(): FuelRecord = FuelRecord(
    id = id,
    vehicleId = vehicleId,
    date = date,
    odometer = odometer,
    volume = volume,
    amount = amount,
    unitPrice = unitPrice,
    isFullTank = isFullTank,
    memo = memo,
)

fun FuelRecord.toEntity(): FuelRecordEntity = FuelRecordEntity(
    id = id,
    vehicleId = vehicleId,
    date = date,
    odometer = odometer,
    volume = volume,
    amount = amount,
    unitPrice = unitPrice,
    isFullTank = isFullTank,
    memo = memo,
)
