package com.segnities007.stylish_myvehicles.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    tableName = "maintenance_schedules",
    foreignKeys = [ForeignKey(
        entity = VehicleEntity::class,
        parentColumns = ["id"],
        childColumns = ["vehicleId"],
        onDelete = ForeignKey.CASCADE,
    )],
    indices = [Index("vehicleId")],
)
data class MaintenanceScheduleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val vehicleId: Long,
    val category: String,
    val intervalKm: Int? = null,
    val intervalMonths: Int? = null,
    val lastDoneDate: LocalDate? = null,
    val lastDoneOdometer: Int? = null,
)
