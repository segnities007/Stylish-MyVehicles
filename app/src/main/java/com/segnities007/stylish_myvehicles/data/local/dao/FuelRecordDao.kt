package com.segnities007.stylish_myvehicles.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.segnities007.stylish_myvehicles.data.local.entity.FuelRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FuelRecordDao {
    @Query("SELECT * FROM fuel_records WHERE vehicleId = :vehicleId ORDER BY date DESC, odometer DESC")
    fun getByVehicleId(vehicleId: Long): Flow<List<FuelRecordEntity>>

    @Query("SELECT * FROM fuel_records WHERE vehicleId = :vehicleId ORDER BY date DESC, odometer DESC LIMIT 1")
    suspend fun getLatest(vehicleId: Long): FuelRecordEntity?

    @Insert
    suspend fun insert(entity: FuelRecordEntity): Long

    @Update
    suspend fun update(entity: FuelRecordEntity)

    @Delete
    suspend fun delete(entity: FuelRecordEntity)
}
