package com.segnities007.stylish_myvehicles.presentation.screen.recordslist

import com.segnities007.stylish_myvehicles.domain.model.CostRecord
import com.segnities007.stylish_myvehicles.domain.model.FuelRecord
import com.segnities007.stylish_myvehicles.domain.model.MaintenanceRecord
import java.time.LocalDate
import java.time.YearMonth

/** 給油・整備・費用を一つのリストで統一的に扱うためのエントリ。 */
sealed interface RecordEntry {
    val id: Long
    val date: LocalDate
}

data class FuelEntry(val record: FuelRecord) : RecordEntry {
    override val id: Long get() = record.id
    override val date: LocalDate get() = record.date
}

data class MaintenanceEntry(val record: MaintenanceRecord) : RecordEntry {
    override val id: Long get() = record.id
    override val date: LocalDate get() = record.date
}

data class CostEntry(val record: CostRecord) : RecordEntry {
    override val id: Long get() = record.id
    override val date: LocalDate get() = record.date
}

/** 月ごとのレコードのまとまり。 */
data class RecordSection(
    val month: YearMonth,
    val entries: List<RecordEntry>,
) {
    val totalAmount: Int
        get() = entries.sumOf { entry ->
            when (entry) {
                is FuelEntry -> entry.record.amount
                is MaintenanceEntry -> entry.record.cost
                is CostEntry -> entry.record.amount
            }
        }
}

data class RecordsListUiState(
    val vehicleId: Long = 0,
    val vehicleName: String = "",
    val isLoading: Boolean = true,
    val sections: List<RecordSection> = emptyList(),
) {
    val totalCount: Int
        get() = sections.sumOf { it.entries.size }
}
