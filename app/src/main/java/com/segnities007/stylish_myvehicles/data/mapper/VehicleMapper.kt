package com.segnities007.stylish_myvehicles.data.mapper

import com.segnities007.stylish_myvehicles.data.local.entity.VehicleEntity
import com.segnities007.stylish_myvehicles.domain.model.Vehicle
import com.segnities007.stylish_myvehicles.domain.model.VehicleCategory
import java.time.LocalDateTime

fun VehicleEntity.toDomain(): Vehicle = Vehicle(
    id = id,
    category = runCatching { VehicleCategory.valueOf(category) }.getOrDefault(VehicleCategory.CAR),
    maker = maker,
    name = name,
    grade = grade,
    year = year,
    modelCode = modelCode,
    plateNumber = plateNumber,
    vin = vin,
    displacement = displacement,
    weight = weight,
    maxLoadKg = maxLoadKg,
    color = color,
    firstRegistrationDate = firstRegistrationDate,
    inspectionExpiry = inspectionExpiry,
    jibaiExpiry = jibaiExpiry,
    insuranceExpiry = insuranceExpiry,
    insuranceCompany = insuranceCompany,
    insuranceRank = insuranceRank,
    taxPaid = taxPaid,
    photoUri = photoUri,
    memo = memo,
)

fun Vehicle.toEntity(): VehicleEntity = VehicleEntity(
    id = id,
    category = category.name,
    maker = maker,
    name = name,
    grade = grade,
    year = year,
    modelCode = modelCode,
    plateNumber = plateNumber,
    vin = vin,
    displacement = displacement,
    weight = weight,
    maxLoadKg = maxLoadKg,
    color = color,
    firstRegistrationDate = firstRegistrationDate,
    inspectionExpiry = inspectionExpiry,
    jibaiExpiry = jibaiExpiry,
    insuranceExpiry = insuranceExpiry,
    insuranceCompany = insuranceCompany,
    insuranceRank = insuranceRank,
    taxPaid = taxPaid,
    photoUri = photoUri,
    memo = memo,
    updatedAt = LocalDateTime.now(),
)
