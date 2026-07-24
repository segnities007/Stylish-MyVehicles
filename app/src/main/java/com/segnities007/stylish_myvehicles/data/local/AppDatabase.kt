package com.segnities007.stylish_myvehicles.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.segnities007.stylish_myvehicles.data.local.dao.CostRecordDao
import com.segnities007.stylish_myvehicles.data.local.dao.FuelRecordDao
import com.segnities007.stylish_myvehicles.data.local.dao.MaintenanceRecordDao
import com.segnities007.stylish_myvehicles.data.local.dao.MaintenanceScheduleDao
import com.segnities007.stylish_myvehicles.data.local.dao.TripRecordDao
import com.segnities007.stylish_myvehicles.data.local.dao.VehicleDao
import com.segnities007.stylish_myvehicles.data.local.entity.CostRecordEntity
import com.segnities007.stylish_myvehicles.data.local.entity.FuelRecordEntity
import com.segnities007.stylish_myvehicles.data.local.entity.MaintenanceRecordEntity
import com.segnities007.stylish_myvehicles.data.local.entity.MaintenanceScheduleEntity
import com.segnities007.stylish_myvehicles.data.local.entity.TripLocationPointEntity
import com.segnities007.stylish_myvehicles.data.local.entity.TripRecordEntity
import com.segnities007.stylish_myvehicles.data.local.entity.VehicleEntity

@Database(
    entities = [
        VehicleEntity::class,
        FuelRecordEntity::class,
        MaintenanceRecordEntity::class,
        CostRecordEntity::class,
        MaintenanceScheduleEntity::class,
        TripRecordEntity::class,
        TripLocationPointEntity::class,
    ],
    version = 7,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun vehicleDao(): VehicleDao
    abstract fun fuelRecordDao(): FuelRecordDao
    abstract fun maintenanceRecordDao(): MaintenanceRecordDao
    abstract fun costRecordDao(): CostRecordDao
    abstract fun maintenanceScheduleDao(): MaintenanceScheduleDao
    abstract fun tripRecordDao(): TripRecordDao

    companion object {
        fun create(context: Context): AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, "stylish_myvehicles.db")
                .addMigrations(MIGRATION_5_6, MIGRATION_6_7)
                .enableMultiInstanceInvalidation()
                .build()

        private val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `trip_records` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `vehicleId` INTEGER NOT NULL,
                        `title` TEXT NOT NULL,
                        `purpose` TEXT NOT NULL,
                        `startedAt` TEXT NOT NULL,
                        `endedAt` TEXT,
                        `distanceMeters` INTEGER NOT NULL,
                        `startOdometer` INTEGER,
                        `endOdometer` INTEGER,
                        `memo` TEXT NOT NULL,
                        FOREIGN KEY(`vehicleId`) REFERENCES `vehicles`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                    """.trimIndent(),
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_trip_records_vehicleId` " +
                        "ON `trip_records` (`vehicleId`)",
                )
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `trip_location_points` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `tripId` INTEGER NOT NULL,
                        `latitude` REAL NOT NULL,
                        `longitude` REAL NOT NULL,
                        `recordedAt` TEXT NOT NULL,
                        `accuracyMeters` REAL NOT NULL,
                        FOREIGN KEY(`tripId`) REFERENCES `trip_records`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                    """.trimIndent(),
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_trip_location_points_tripId` " +
                        "ON `trip_location_points` (`tripId`)",
                )
            }
        }

        private val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE `fuel_records` ADD COLUMN `fuelEconomy` REAL DEFAULT NULL",
                )
            }
        }
    }
}
