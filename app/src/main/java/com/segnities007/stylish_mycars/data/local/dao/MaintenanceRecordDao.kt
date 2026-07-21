package com.segnities007.stylish_mycars.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.segnities007.stylish_mycars.data.local.entity.MaintenanceRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MaintenanceRecordDao {
    @Query("SELECT * FROM maintenance_records WHERE vehicleId = :vehicleId ORDER BY date DESC")
    fun getByVehicleId(vehicleId: Long): Flow<List<MaintenanceRecordEntity>>

    @Insert
    suspend fun insert(entity: MaintenanceRecordEntity): Long

    @Update
    suspend fun update(entity: MaintenanceRecordEntity)

    @Delete
    suspend fun delete(entity: MaintenanceRecordEntity)
}
