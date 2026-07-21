package com.segnities007.stylish_mycars.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.segnities007.stylish_mycars.data.local.entity.MaintenanceScheduleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MaintenanceScheduleDao {
    @Query("SELECT * FROM maintenance_schedules WHERE vehicleId = :vehicleId")
    fun getByVehicleId(vehicleId: Long): Flow<List<MaintenanceScheduleEntity>>

    @Query("SELECT * FROM maintenance_schedules")
    suspend fun getAll(): List<MaintenanceScheduleEntity>

    @Insert
    suspend fun insert(entity: MaintenanceScheduleEntity): Long

    @Insert
    suspend fun insertAll(entities: List<MaintenanceScheduleEntity>)

    @Update
    suspend fun update(entity: MaintenanceScheduleEntity)

    @Query("UPDATE maintenance_schedules SET lastDoneDate = :date, lastDoneOdometer = :odometer WHERE vehicleId = :vehicleId AND category = :category")
    suspend fun updateLastDone(vehicleId: Long, category: String, date: java.time.LocalDate, odometer: Int?)
}
