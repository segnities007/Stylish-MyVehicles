package com.segnities007.stylish_myvehicles.data.mapper

import com.segnities007.stylish_myvehicles.data.local.entity.MaintenanceRecordEntity
import com.segnities007.stylish_myvehicles.domain.model.MaintenanceCategory
import com.segnities007.stylish_myvehicles.domain.model.MaintenanceRecord

fun MaintenanceRecordEntity.toDomain(): MaintenanceRecord = MaintenanceRecord(
    id = id,
    vehicleId = vehicleId,
    date = date,
    odometer = odometer,
    category = MaintenanceCategory.valueOf(category),
    title = title,
    cost = cost,
    shopName = shopName,
    memo = memo,
    photoUri = photoUri,
)

fun MaintenanceRecord.toEntity(): MaintenanceRecordEntity = MaintenanceRecordEntity(
    id = id,
    vehicleId = vehicleId,
    date = date,
    odometer = odometer,
    category = category.name,
    title = title,
    cost = cost,
    shopName = shopName,
    memo = memo,
    photoUri = photoUri,
)
