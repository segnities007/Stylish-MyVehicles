package com.segnities007.stylish_mycars.domain.model

import java.time.LocalDate

data class CostRecord(
    val id: Long = 0,
    val vehicleId: Long,
    val date: LocalDate,
    val category: CostCategory,
    val title: String,
    val amount: Int,
    val memo: String = "",
)
