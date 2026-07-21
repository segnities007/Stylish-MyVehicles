package com.segnities007.stylish_mycars.data.repository

import com.segnities007.stylish_mycars.data.local.dao.MaintenanceScheduleDao
import com.segnities007.stylish_mycars.data.local.entity.MaintenanceScheduleEntity
import com.segnities007.stylish_mycars.domain.model.MaintenanceCategory
import com.segnities007.stylish_mycars.domain.model.MaintenanceSchedule
import com.segnities007.stylish_mycars.domain.model.VehicleCategory
import com.segnities007.stylish_mycars.domain.repository.MaintenanceScheduleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class MaintenanceScheduleRepositoryImpl(
    private val dao: MaintenanceScheduleDao,
) : MaintenanceScheduleRepository {

    override fun getByVehicleId(vehicleId: Long): Flow<List<MaintenanceSchedule>> =
        dao.getByVehicleId(vehicleId)
            .map { entities -> entities.map { it.toDomain() } }

    override suspend fun getAll(): List<MaintenanceSchedule> =
        dao.getAll()
            .map { it.toDomain() }

    override suspend fun insertDefaults(vehicleId: Long, category: VehicleCategory) {
        val entities = MaintenanceSchedule.defaults(vehicleId, category)
            .map { schedule ->
                MaintenanceScheduleEntity(
                    vehicleId = schedule.vehicleId,
                    category = schedule.category.name,
                    intervalKm = schedule.intervalKm,
                    intervalMonths = schedule.intervalMonths,
                )
            }
        dao.insertAll(entities)
    }

    override suspend fun update(schedule: MaintenanceSchedule) {
        dao.update(
            MaintenanceScheduleEntity(
                id = schedule.id,
                vehicleId = schedule.vehicleId,
                category = schedule.category.name,
                intervalKm = schedule.intervalKm,
                intervalMonths = schedule.intervalMonths,
                lastDoneDate = schedule.lastDoneDate,
                lastDoneOdometer = schedule.lastDoneOdometer,
            ),
        )
    }

    override suspend fun updateLastDone(
        vehicleId: Long, category: String, date: LocalDate, odometer: Int?,
    ) = dao.updateLastDone(vehicleId, category, date, odometer)

    private fun MaintenanceScheduleEntity.toDomain() = MaintenanceSchedule(
        id = id,
        vehicleId = vehicleId,
        category = MaintenanceCategory.valueOf(category),
        intervalKm = intervalKm,
        intervalMonths = intervalMonths,
        lastDoneDate = lastDoneDate,
        lastDoneOdometer = lastDoneOdometer,
    )
}
