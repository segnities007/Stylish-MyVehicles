package com.segnities007.stylish_mycars.domain.usecase.maintenance

import com.segnities007.stylish_mycars.domain.model.CostCategory
import com.segnities007.stylish_mycars.domain.model.CostRecord
import com.segnities007.stylish_mycars.domain.model.MaintenanceRecord
import com.segnities007.stylish_mycars.domain.repository.CostRecordRepository
import com.segnities007.stylish_mycars.domain.repository.MaintenanceRecordRepository
import com.segnities007.stylish_mycars.domain.repository.MaintenanceScheduleRepository

class InsertMaintenanceRecordUseCase(
    private val maintenanceRepository: MaintenanceRecordRepository,
    private val costRepository: CostRecordRepository,
    private val scheduleRepository: MaintenanceScheduleRepository,
) {
    suspend operator fun invoke(record: MaintenanceRecord): Long {
        val id = maintenanceRepository.insert(record)
        if (record.cost > 0) {
            costRepository.insert(
                CostRecord(
                    vehicleId = record.vehicleId,
                    date = record.date,
                    category = CostCategory.MAINTENANCE,
                    title = record.title,
                    amount = record.cost,
                ),
            )
        }
        scheduleRepository.updateLastDone(
            vehicleId = record.vehicleId,
            category = record.category.name,
            date = record.date,
            odometer = record.odometer,
        )
        return id
    }
}
