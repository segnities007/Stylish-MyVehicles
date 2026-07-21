package com.segnities007.stylish_myvehicles.domain.model

import java.time.LocalDate

data class FuelRecord(
    val id: Long = 0,
    val vehicleId: Long,
    val date: LocalDate,
    val odometer: Int,
    val volume: Double,
    val amount: Int,
    val unitPrice: Int? = null,
    val fuelEconomy: Double? = null,
    val isFullTank: Boolean = true,
    val memo: String = "",
)
