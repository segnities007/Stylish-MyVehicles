package com.segnities007.stylish_myvehicles.domain.service

import com.segnities007.stylish_myvehicles.domain.model.CostCategory
import com.segnities007.stylish_myvehicles.domain.model.CostRecord
import com.segnities007.stylish_myvehicles.domain.model.FuelRecord
import com.segnities007.stylish_myvehicles.domain.model.MaintenanceCategory
import com.segnities007.stylish_myvehicles.domain.model.MaintenanceSchedule
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DashboardCalculatorTest {

    private val now = LocalDate.now()

    private fun cost(
        category: CostCategory,
        amount: Int,
        date: LocalDate = now,
    ) = CostRecord(vehicleId = 1, date = date, category = category, title = "t", amount = amount)

    private fun fuel(
        odometer: Int,
        fuelEconomy: Double? = null,
        date: LocalDate = now,
    ) = FuelRecord(
        vehicleId = 1,
        date = date,
        odometer = odometer,
        volume = 30.0,
        amount = 5000,
        fuelEconomy = fuelEconomy,
    )

    // ── 空リスト ──

    @Test
    fun `空のリストでビルドするとゼロのダッシュボードが返る`() {
        // Arrange（準備）
        // Act（実行）
        val dashboard = DashboardCalculator.build(
            costs = emptyList(),
            fuels = emptyList(),
            schedules = emptyList(),
        )

        // Assert（検証）
        assertEquals(0, dashboard.monthlyCost)
        assertEquals(0, dashboard.yearlyCost)
        assertEquals(0, dashboard.totalCost)
        assertNull(dashboard.averageFuelEconomy)
        assertEquals(0, dashboard.totalDistance)
        assertNull(dashboard.costPerKm)
        assertNull(dashboard.nextMaintenanceLabel)
        assertNull(dashboard.nextMaintenanceDays)
        assertTrue(dashboard.costByCategory.isEmpty())
    }

    // ── 費用計算 ──

    @Test
    fun `費用レコードから月次・年間・総費用が正しく計算される`() {
        // Arrange（準備）
        val thisMonth = now.withDayOfMonth(15)
        val lastMonth = now.minusMonths(1).withDayOfMonth(15)
        val lastYear = now.minusYears(1).withDayOfMonth(15)
        val costs = listOf(
            cost(CostCategory.FUEL, 5_000, thisMonth),
            cost(CostCategory.INSURANCE, 3_000, thisMonth),
            cost(CostCategory.FUEL, 4_000, lastMonth),
            cost(CostCategory.TAX, 45_000, lastYear),
        )

        // Act（実行）
        val dashboard = DashboardCalculator.build(costs, emptyList(), emptyList())

        // Assert（検証）
        assertEquals(8_000, dashboard.monthlyCost)
        assertEquals(12_000, dashboard.yearlyCost)
        assertEquals(57_000, dashboard.totalCost)
    }

    // ── 燃費計算 ──

    @Test
    fun `燃費レコードから平均燃費と総走行距離が計算される`() {
        // Arrange（準備）
        val fuels = listOf(
            fuel(odometer = 40_000, fuelEconomy = 12.0),
            fuel(odometer = 40_500, fuelEconomy = 14.0),
            fuel(odometer = 41_000, fuelEconomy = 16.0),
        )

        // Act（実行）
        val dashboard = DashboardCalculator.build(emptyList(), fuels, emptyList())

        // Assert（検証）
        assertEquals(14.0, dashboard.averageFuelEconomy!!, 0.001)
        assertEquals(1_000, dashboard.totalDistance)
    }

    @Test
    fun `燃費レコードが1件の場合総走行距離はゼロ`() {
        // Arrange（準備）
        val fuels = listOf(fuel(odometer = 40_000, fuelEconomy = 12.0))

        // Act（実行）
        val dashboard = DashboardCalculator.build(emptyList(), fuels, emptyList())

        // Assert（検証）
        assertEquals(0, dashboard.totalDistance)
    }

    @Test
    fun `fuelEconomyが全てnullの場合平均燃費はnull`() {
        // Arrange（準備）
        val fuels = listOf(
            fuel(odometer = 40_000, fuelEconomy = null),
            fuel(odometer = 41_000, fuelEconomy = null),
        )

        // Act（実行）
        val dashboard = DashboardCalculator.build(emptyList(), fuels, emptyList())

        // Assert（検証）
        assertNull(dashboard.averageFuelEconomy)
    }

    // ── カテゴリ別費用 ──

    @Test
    fun `カテゴリ別費用の集計が正しい`() {
        // Arrange（準備）
        val costs = listOf(
            cost(CostCategory.FUEL, 5_000),
            cost(CostCategory.FUEL, 3_000),
            cost(CostCategory.INSURANCE, 12_000),
            cost(CostCategory.TAX, 45_000),
        )

        // Act（実行）
        val dashboard = DashboardCalculator.build(costs, emptyList(), emptyList())

        // Assert（検証）
        assertEquals(3, dashboard.costByCategory.size)
        // 降順ソートされていること
        assertEquals(CostCategory.TAX to 45_000, dashboard.costByCategory[0])
        assertEquals(CostCategory.INSURANCE to 12_000, dashboard.costByCategory[1])
        assertEquals(CostCategory.FUEL to 8_000, dashboard.costByCategory[2])
    }

    // ── 月次費用トレンド ──

    @Test
    fun `月次費用トレンドが正しい`() {
        // Arrange（準備）
        val thisMonth = now.withDayOfMonth(10)
        val lastMonth = now.minusMonths(1).withDayOfMonth(10)
        val costs = listOf(
            cost(CostCategory.FUEL, 5_000, thisMonth),
            cost(CostCategory.FUEL, 3_000, lastMonth),
        )

        // Act（実行）
        val dashboard = DashboardCalculator.build(costs, emptyList(), emptyList())

        // Assert（検証）
        assertEquals(6, dashboard.monthlyCostTrend.size)
        // 最後のエントリが当月
        val lastEntry = dashboard.monthlyCostTrend.last()
        assertEquals("${now.monthValue}月", lastEntry.first)
        assertEquals(5_000f, lastEntry.second, 0.01f)
        // 1つ前が前月
        val prevEntry = dashboard.monthlyCostTrend[4]
        assertEquals("${lastMonth.monthValue}月", prevEntry.first)
        assertEquals(3_000f, prevEntry.second, 0.01f)
    }

    // ── 燃費トレンド ──

    @Test
    fun `燃費トレンドが正しい`() {
        // Arrange（準備）
        val fuels = listOf(
            fuel(odometer = 40_000, fuelEconomy = 12.0, date = LocalDate.of(2026, 7, 1)),
            fuel(odometer = 40_500, fuelEconomy = 14.5, date = LocalDate.of(2026, 7, 10)),
            fuel(odometer = 41_000, fuelEconomy = null, date = LocalDate.of(2026, 7, 15)),
        )

        // Act（実行）
        val dashboard = DashboardCalculator.build(emptyList(), fuels, emptyList())

        // Assert（検証）
        // fuelEconomy が null のレコードは除外され、reversed される
        assertEquals(2, dashboard.fuelEconomyTrend.size)
        assertEquals("7/10" to 14.5f, dashboard.fuelEconomyTrend[0])
        assertEquals("7/1" to 12.0f, dashboard.fuelEconomyTrend[1])
    }

    // ── 次回整備 ──

    @Test
    fun `次回整備の計算が正しい`() {
        // Arrange（準備）
        val schedules = listOf(
            MaintenanceSchedule(
                vehicleId = 1,
                category = MaintenanceCategory.OIL,
                intervalMonths = 6,
                lastDoneDate = now.minusMonths(5),
            ),
            MaintenanceSchedule(
                vehicleId = 1,
                category = MaintenanceCategory.TIRE,
                intervalMonths = 36,
                lastDoneDate = now.minusMonths(12),
            ),
        )

        // Act（実行）
        val dashboard = DashboardCalculator.build(emptyList(), emptyList(), schedules)

        // Assert（検証）
        // OIL: 6ヶ月 - 5ヶ月 = 約30日、TIRE: 36ヶ月 - 12ヶ月 = 約730日
        // 最短の OIL が選ばれる
        assertEquals(MaintenanceCategory.OIL.label, dashboard.nextMaintenanceLabel)
        assertTrue(dashboard.nextMaintenanceDays!! <= 31)
        assertTrue(dashboard.nextMaintenanceDays!! >= 28)
    }

    @Test
    fun `期限計算できないスケジュールは次回整備に含まれない`() {
        // Arrange（準備）
        val schedules = listOf(
            MaintenanceSchedule(
                vehicleId = 1,
                category = MaintenanceCategory.BRAKE,
                intervalKm = 30_000,
                // intervalMonths も lastDoneDate もない → dueDate = null
            ),
        )

        // Act（実行）
        val dashboard = DashboardCalculator.build(emptyList(), emptyList(), schedules)

        // Assert（検証）
        assertNull(dashboard.nextMaintenanceLabel)
        assertNull(dashboard.nextMaintenanceDays)
    }
}
