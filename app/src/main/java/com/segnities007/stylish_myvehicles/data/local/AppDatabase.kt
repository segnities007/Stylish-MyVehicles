package com.segnities007.stylish_myvehicles.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.segnities007.stylish_myvehicles.data.local.dao.CostRecordDao
import com.segnities007.stylish_myvehicles.data.local.dao.FuelRecordDao
import com.segnities007.stylish_myvehicles.data.local.dao.MaintenanceRecordDao
import com.segnities007.stylish_myvehicles.data.local.dao.MaintenanceScheduleDao
import com.segnities007.stylish_myvehicles.data.local.dao.VehicleDao
import com.segnities007.stylish_myvehicles.data.local.entity.CostRecordEntity
import com.segnities007.stylish_myvehicles.data.local.entity.FuelRecordEntity
import com.segnities007.stylish_myvehicles.data.local.entity.MaintenanceRecordEntity
import com.segnities007.stylish_myvehicles.data.local.entity.MaintenanceScheduleEntity
import com.segnities007.stylish_myvehicles.data.local.entity.VehicleEntity

@Database(
    entities = [
        VehicleEntity::class,
        FuelRecordEntity::class,
        MaintenanceRecordEntity::class,
        CostRecordEntity::class,
        MaintenanceScheduleEntity::class,
    ],
    version = 5,
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun vehicleDao(): VehicleDao
    abstract fun fuelRecordDao(): FuelRecordDao
    abstract fun maintenanceRecordDao(): MaintenanceRecordDao
    abstract fun costRecordDao(): CostRecordDao
    abstract fun maintenanceScheduleDao(): MaintenanceScheduleDao

    companion object {
        fun create(context: Context): AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, "stylish_myvehicles.db")
                .fallbackToDestructiveMigration()
                .build()
    }
}
