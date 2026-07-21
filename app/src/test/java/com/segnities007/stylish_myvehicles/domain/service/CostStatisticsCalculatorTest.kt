package com.segnities007.stylish_myvehicles.domain.service

import com.segnities007.stylish_myvehicles.domain.model.CostCategory
import com.segnities007.stylish_myvehicles.domain.model.CostRecord
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CostStatisticsCalculatorTest {

    private val today = LocalDate.of(2026, 7, 20)

    private fun record(category: CostCategory, amount: Int, date: LocalDate) =
        CostRecord(vehicleId = 1, date = date, category = category, title = "t", amount = amount)

    // ── 月平均費用 ──
    @Test
    fun `average monthly cost is null for empty records`() {
        assertNull(CostStatisticsCalculator.averageMonthlyCost(emptyList(), today))
    }

    @Test
    fun `average monthly cost spans from first record to today`() {
        // 2026-01 に 12,000、2026-03 に 6,000 → 合計 18,000
        // 期間 2026-01 〜 2026-07 = 7ヶ月 → 18,000 / 7 ≒ 2571.4
        val records = listOf(
            record(CostCategory.FUEL, 12_000, LocalDate.of(2026, 1, 10)),
            record(CostCategory.FUEL, 6_000, LocalDate.of(2026, 3, 10)),
        )
        val avg = CostStatisticsCalculator.averageMonthlyCost(records, today)!!
        assertEquals(18_000.0 / 7, avg, 0.01)
    }

    @Test
    fun `average monthly cost uses at least one month`() {
        val records = listOf(record(CostCategory.TAX, 45_000, today))
        val avg = CostStatisticsCalculator.averageMonthlyCost(records, today)!!
        assertEquals(45_000.0, avg, 0.01)
    }

    // ── カテゴリ別合計 ──
    @Test
    fun `total by category groups amounts`() {
        val records = listOf(
            record(CostCategory.FUEL, 5_000, today),
            record(CostCategory.FUEL, 3_000, today),
            record(CostCategory.INSURANCE, 12_000, today),
        )
        val totals = CostStatisticsCalculator.totalByCategory(records)
        assertEquals(8_000, totals[CostCategory.FUEL])
        assertEquals(12_000, totals[CostCategory.INSURANCE])
        assertEquals(2, totals.size)
    }

    // ── 年別合計 ──
    @Test
    fun `total by year groups amounts`() {
        val records = listOf(
            record(CostCategory.FUEL, 5_000, LocalDate.of(2025, 6, 1)),
            record(CostCategory.FUEL, 7_000, LocalDate.of(2026, 6, 1)),
            record(CostCategory.TAX, 3_000, LocalDate.of(2026, 5, 1)),
        )
        val totals = CostStatisticsCalculator.totalByYear(records)
        assertEquals(5_000, totals[2025])
        assertEquals(10_000, totals[2026])
    }

    // ── 1kmあたりコスト ──
    @Test
    fun `cost per km divides total cost by distance`() {
        assertEquals(10.0, CostStatisticsCalculator.costPerKm(100_000, 10_000)!!, 0.01)
    }

    @Test
    fun `cost per km is null for zero distance`() {
        assertNull(CostStatisticsCalculator.costPerKm(100_000, 0))
    }
}
