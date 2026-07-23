package com.segnities007.stylish_myvehicles.domain.usecase.trip

import com.segnities007.stylish_myvehicles.domain.model.TripRecord
import com.segnities007.stylish_myvehicles.domain.repository.TripRecordRepository

class GetTripRecordsUseCase(private val repository: TripRecordRepository) {
    operator fun invoke(vehicleId: Long) = repository.getByVehicleId(vehicleId)
}

class InsertTripRecordUseCase(private val repository: TripRecordRepository) {
    suspend operator fun invoke(record: TripRecord) = repository.insert(record)
}

class UpdateTripRecordUseCase(private val repository: TripRecordRepository) {
    suspend operator fun invoke(record: TripRecord) = repository.update(record)
}

class DeleteTripRecordUseCase(private val repository: TripRecordRepository) {
    suspend operator fun invoke(record: TripRecord) = repository.delete(record)
}
