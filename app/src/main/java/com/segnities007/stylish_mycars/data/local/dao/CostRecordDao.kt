package com.segnities007.stylish_mycars.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.segnities007.stylish_mycars.data.local.entity.CostRecordEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface CostRecordDao {
    @Query("SELECT * FROM cost_records WHERE vehicleId = :vehicleId ORDER BY date DESC")
    fun getByVehicleId(vehicleId: Long): Flow<List<CostRecordEntity>>

    @Query(
        """
        SELECT * FROM cost_records
        WHERE vehicleId = :vehicleId
          AND date BETWEEN :start AND :end
        ORDER BY date DESC
    """
    )
    fun getByVehicleIdAndDateRange(
        vehicleId: Long,
        start: LocalDate,
        end: LocalDate,
    ): Flow<List<CostRecordEntity>>

    @Insert
    suspend fun insert(entity: CostRecordEntity): Long

    @Update
    suspend fun update(entity: CostRecordEntity)

    @Delete
    suspend fun delete(entity: CostRecordEntity)
}
