package com.segnities007.stylish_myvehicles.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.segnities007.stylish_myvehicles.data.local.entity.TripLocationPointEntity
import com.segnities007.stylish_myvehicles.data.local.entity.TripRecordEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface TripRecordDao {
    @Query("SELECT * FROM trip_records WHERE vehicleId = :vehicleId ORDER BY startedAt DESC")
    fun getByVehicleId(vehicleId: Long): Flow<List<TripRecordEntity>>

    @Query("SELECT * FROM trip_records WHERE id = :tripId LIMIT 1")
    suspend fun getById(tripId: Long): TripRecordEntity?

    @Insert
    suspend fun insert(entity: TripRecordEntity): Long

    @Update
    suspend fun update(entity: TripRecordEntity)

    @Delete
    suspend fun delete(entity: TripRecordEntity)

    @Insert
    suspend fun insertLocation(point: TripLocationPointEntity)

    @Query("UPDATE trip_records SET distanceMeters = :distanceMeters WHERE id = :tripId")
    suspend fun updateDistance(tripId: Long, distanceMeters: Long)

    @Query(
        "UPDATE trip_records SET endedAt = :endedAt, distanceMeters = :distanceMeters " +
            "WHERE id = :tripId",
    )
    suspend fun finish(tripId: Long, endedAt: LocalDateTime, distanceMeters: Long)
}
