package com.segnities007.stylish_mycars.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime

@Entity(
    tableName = "cost_records",
    foreignKeys = [ForeignKey(
        entity = VehicleEntity::class,
        parentColumns = ["id"],
        childColumns = ["vehicleId"],
        onDelete = ForeignKey.CASCADE,
    )],
    indices = [Index("vehicleId")],
)
data class CostRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val vehicleId: Long,
    val date: LocalDate,
    val category: String,
    val title: String,
    val amount: Int,
    val memo: String = "",
    val createdAt: LocalDateTime = LocalDateTime.now(),
)
