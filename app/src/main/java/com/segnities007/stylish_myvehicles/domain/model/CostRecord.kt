package com.segnities007.stylish_myvehicles.domain.model

import androidx.compose.runtime.Immutable
import java.time.LocalDate

@Immutable
data class CostRecord(
    val id: Long = 0,
    val vehicleId: Long,
    val date: LocalDate,
    val category: CostCategory,
    val title: String,
    val amount: Int,
    val memo: String = "",
)
