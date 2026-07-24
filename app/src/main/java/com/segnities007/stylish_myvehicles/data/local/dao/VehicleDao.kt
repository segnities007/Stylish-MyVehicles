package com.segnities007.stylish_myvehicles.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.segnities007.stylish_myvehicles.data.local.entity.VehicleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VehicleDao {
    @Query("SELECT * FROM vehicles ORDER BY createdAt DESC")
    fun getAll(): Flow<List<VehicleEntity>>

    @Query("SELECT * FROM vehicles WHERE id = :id")
    fun getById(id: Long): Flow<VehicleEntity?>

    @Insert
    suspend fun insert(entity: VehicleEntity): Long

    @Update
    suspend fun update(entity: VehicleEntity)

    @Delete
    suspend fun delete(entity: VehicleEntity)

    @Query("DELETE FROM vehicles WHERE id = :id")
    suspend fun deleteById(id: Long)
}
