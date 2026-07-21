package com.segnities007.stylish_myvehicles.domain.model

import java.time.LocalDate

data class MaintenanceRecord(
    val id: Long = 0,
    val vehicleId: Long,
    val date: LocalDate,
    val odometer: Int? = null,
    val category: MaintenanceCategory,
    val title: String,
    val cost: Int = 0,
    val shopName: String = "",
    val memo: String = "",
    val photoUri: String? = null,
)
