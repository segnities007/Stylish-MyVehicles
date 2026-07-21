package com.segnities007.stylish_mycars.domain.service

import com.segnities007.stylish_mycars.domain.model.CostCategory
import com.segnities007.stylish_mycars.domain.model.CostRecord
import java.time.LocalDate
import java.time.temporal.ChronoUnit

/** 費用レコードから各種統計量を計算するドメインサービス。 */
object CostStatisticsCalculator {

    /**
     * 月平均費用。最初の記録から [today] までの月数で按分する（最低1ヶ月）。
     * 記録がなければ null。
     */
    fun averageMonthlyCost(records: List<CostRecord>, today: LocalDate = LocalDate.now()): Double? {
        if (records.isEmpty()) return null
        val total = records.sumOf { it.amount }
            .toDouble()
        val firstDate = records.minOf { it.date }
        // 最初の記録の月から今月まで（両端含む）の月数で按分する
        val months = ChronoUnit.MONTHS.between(
            firstDate.withDayOfMonth(1),
            today.withDayOfMonth(1),
        )
            .toInt() + 1
        return total / months.coerceAtLeast(1)
    }

    /** カテゴリ別の合計費用。 */
    fun totalByCategory(records: List<CostRecord>): Map<CostCategory, Int> =
        records.groupBy({ it.category }, { it.amount })
            .mapValues { (_, amounts) -> amounts.sum() }

    /** 年別の合計費用。 */
    fun totalByYear(records: List<CostRecord>): Map<Int, Int> =
        records.groupBy({ it.date.year }, { it.amount })
            .mapValues { (_, amounts) -> amounts.sum() }

    /** 1kmあたりのコスト。走行距離が0以下なら null。 */
    fun costPerKm(totalCost: Int, totalDistance: Int): Double? =
        if (totalDistance > 0) totalCost.toDouble() / totalDistance else null
}
