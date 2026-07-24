package com.segnities007.stylish_myvehicles.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(
    tableName = "trip_records",
    foreignKeys = [ForeignKey(
        entity = VehicleEntity::class,
        parentColumns = ["id"],
        childColumns = ["vehicleId"],
        onDelete = ForeignKey.CASCADE,
    )],
    indices = [Index("vehicleId")],
)
data class TripRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val vehicleId: Long,
    val title: String,
    val purpose: String,
    val startedAt: LocalDateTime,
    val endedAt: LocalDateTime?,
    val distanceMeters: Long,
    val startOdometer: Int?,
    val endOdometer: Int?,
    val memo: String,
)

@Entity(
    tableName = "trip_location_points",
    foreignKeys = [ForeignKey(
        entity = TripRecordEntity::class,
        parentColumns = ["id"],
        childColumns = ["tripId"],
        onDelete = ForeignKey.CASCADE,
    )],
    indices = [Index("tripId")],
)
data class TripLocationPointEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tripId: Long,
    val latitude: Double,
    val longitude: Double,
    val recordedAt: LocalDateTime,
    val accuracyMeters: Float,
)
