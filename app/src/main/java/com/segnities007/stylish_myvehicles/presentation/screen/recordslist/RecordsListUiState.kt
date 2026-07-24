package com.segnities007.stylish_myvehicles.presentation.screen.recordslist

import androidx.compose.runtime.Immutable
import com.segnities007.stylish_myvehicles.domain.model.CostRecord
import com.segnities007.stylish_myvehicles.domain.model.FuelRecord
import com.segnities007.stylish_myvehicles.domain.model.MaintenanceRecord
import com.segnities007.stylish_myvehicles.domain.model.TripRecord
import java.time.LocalDate
import java.time.YearMonth

/** 全車両の給油・整備・費用を一つのリストで統一的に扱うためのエントリ。 */
@Immutable
sealed interface RecordEntry {
    val id: Long
    val date: LocalDate
    val vehicleName: String
}

@Immutable
data class FuelEntry(
    val record: FuelRecord,
    override val vehicleName: String,
) : RecordEntry {
    override val id: Long get() = record.id
    override val date: LocalDate get() = record.date
}

@Immutable
data class MaintenanceEntry(
    val record: MaintenanceRecord,
    override val vehicleName: String,
) : RecordEntry {
    override val id: Long get() = record.id
    override val date: LocalDate get() = record.date
}

@Immutable
data class CostEntry(
    val record: CostRecord,
    override val vehicleName: String,
) : RecordEntry {
    override val id: Long get() = record.id
    override val date: LocalDate get() = record.date
}

@Immutable
data class TripEntry(
    val record: TripRecord,
    override val vehicleName: String,
) : RecordEntry {
    override val id: Long get() = record.id
    override val date: LocalDate get() = record.startedAt.toLocalDate()
}

/** 月ごとのレコードのまとまり。 */
@Immutable
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
                is TripEntry -> 0
            }
        }
}

/** 履歴一覧画面の状態。全車両の記録を時系列で集約する。 */
@Immutable
data class RecordsListUiState(
    val isLoading: Boolean = true,
    val sections: List<RecordSection> = emptyList(),
) {
    val totalCount: Int
        get() = sections.sumOf { it.entries.size }
}
