package com.segnities007.stylish_myvehicles.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime

@Entity(tableName = "vehicles")
data class VehicleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val category: String = "CAR",
    val maker: String,
    val name: String,
    val grade: String = "",
    val year: Int? = null,
    val modelCode: String = "",
    val plateNumber: String = "",
    val vin: String = "",
    val displacement: Int? = null,
    val weight: Int? = null,
    val maxLoadKg: Int? = null,
    val color: String = "",
    val firstRegistrationDate: LocalDate? = null,
    val inspectionExpiry: LocalDate? = null,
    val jibaiExpiry: LocalDate? = null,
    val insuranceExpiry: LocalDate? = null,
    val insuranceCompany: String = "",
    val insuranceRank: Int? = null,
    val taxPaid: Boolean = false,
    val photoUri: String? = null,
    val memo: String = "",
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
)
