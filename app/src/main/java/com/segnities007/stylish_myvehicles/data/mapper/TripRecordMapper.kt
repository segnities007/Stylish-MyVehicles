package com.segnities007.stylish_myvehicles.data.mapper

import com.segnities007.stylish_myvehicles.data.local.entity.TripRecordEntity
import com.segnities007.stylish_myvehicles.domain.model.TripPurpose
import com.segnities007.stylish_myvehicles.domain.model.TripRecord

fun TripRecordEntity.toDomain() = TripRecord(
    id = id,
    vehicleId = vehicleId,
    title = title,
    purpose = TripPurpose.entries.find { it.name == purpose } ?: TripPurpose.OTHER,
    startedAt = startedAt,
    endedAt = endedAt,
    distanceMeters = distanceMeters,
    startOdometer = startOdometer,
    endOdometer = endOdometer,
    memo = memo,
)

fun TripRecord.toEntity() = TripRecordEntity(
    id = id,
    vehicleId = vehicleId,
    title = title,
    purpose = purpose.name,
    startedAt = startedAt,
    endedAt = endedAt,
    distanceMeters = distanceMeters,
    startOdometer = startOdometer,
    endOdometer = endOdometer,
    memo = memo,
)
