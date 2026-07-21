package com.segnities007.stylish_mycars.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime

@Entity(
    tableName = "maintenance_records",
    foreignKeys = [ForeignKey(
        entity = VehicleEntity::class,
        parentColumns = ["id"],
        childColumns = ["vehicleId"],
        onDelete = ForeignKey.CASCADE,
    )],
    indices = [Index("vehicleId")],
)
data class MaintenanceRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val vehicleId: Long,
    val date: LocalDate,
    val odometer: Int? = null,
    val category: String,
    val title: String,
    val cost: Int = 0,
    val shopName: String = "",
    val memo: String = "",
    val photoUri: String? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
)
