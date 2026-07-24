package com.segnities007.stylish_myvehicles.presentation.screen.vehiclepager

import com.segnities007.stylish_myvehicles.domain.model.CostCategory
import com.segnities007.stylish_myvehicles.domain.model.Vehicle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class VehiclePagerUiStateTest {

    private val vehicle1 = Vehicle(id = 1, maker = "トヨタ", name = "プリウス")
    private val vehicle2 = Vehicle(id = 2, maker = "ホンダ", name = "フィット")

    // ─── currentVehicle ────────────────────────────────────────────────

    @Test
    fun `currentVehicleはcurrentPageに対応する車両を返す`() {
        // Arrange（準備）
        val state = VehiclePagerUiState(
            vehicles = listOf(vehicle1, vehicle2),
            currentPage = 1,
        )

        // Act & Assert（実行・検証）
        assertEquals(vehicle2, state.currentVehicle)
    }

    @Test
    fun `currentPageが0の場合最初の車両を返す`() {
        // Arrange（準備）
        val state = VehiclePagerUiState(
            vehicles = listOf(vehicle1, vehicle2),
            currentPage = 0,
        )

        // Act & Assert（実行・検証）
        assertEquals(vehicle1, state.currentVehicle)
    }

    @Test
    fun `currentPageが範囲外の場合nullを返す`() {
        // Arrange（準備）
        val state = VehiclePagerUiState(
            vehicles = listOf(vehicle1),
            currentPage = 5,
        )

        // Act & Assert（実行・検証）
        assertNull(state.currentVehicle)
    }

    @Test
    fun `車両リストが空の場合currentVehicleはnull`() {
        // Arrange（準備）
        val state = VehiclePagerUiState(vehicles = emptyList(), currentPage = 0)

        // Act & Assert（実行・検証）
        assertNull(state.currentVehicle)
    }

    // ─── dashboardFor ──────────────────────────────────────────────────

    @Test
    fun `dashboardForは対応するダッシュボードを返す`() {
        // Arrange（準備）
        val dashboard = VehicleDashboard(monthlyCost = 15000, yearlyCost = 180000)
        val state = VehiclePagerUiState(
            vehicles = listOf(vehicle1),
            dashboardByVehicle = mapOf(1L to dashboard),
        )

        // Act（実行）
        val result = state.dashboardFor(1L)

        // Assert（検証）
        assertEquals(15000, result.monthlyCost)
        assertEquals(180000, result.yearlyCost)
    }

    @Test
    fun `dashboardForは対応するデータがなければデフォルトを返す`() {
        // Arrange（準備）
        val state = VehiclePagerUiState(
            vehicles = listOf(vehicle1),
            dashboardByVehicle = emptyMap(),
        )

        // Act（実行）
        val result = state.dashboardFor(99L)

        // Assert（検証）
        assertEquals(0, result.monthlyCost)
        assertEquals(0, result.yearlyCost)
        assertNull(result.averageFuelEconomy)
    }

    // ─── MonthlyCostSlice.total ────────────────────────────────────────

    @Test
    fun `MonthlyCostSliceのtotalはカテゴリ別金額の合計を返す`() {
        // Arrange（準備）
        val slice = MonthlyCostSlice(
            label = "7月",
            byCategory = listOf(
                CostCategory.FUEL to 10000,
                CostCategory.INSURANCE to 5000,
                CostCategory.PARKING to 8000,
            ),
        )

        // Act & Assert（実行・検証）
        assertEquals(23000, slice.total)
    }

    @Test
    fun `カテゴリが空の場合totalは0`() {
        // Arrange（準備）
        val slice = MonthlyCostSlice(label = "7月", byCategory = emptyList())

        // Act & Assert（実行・検証）
        assertEquals(0, slice.total)
    }

    // ─── VehicleDashboard デフォルト値 ─────────────────────────────────

    @Test
    fun `VehicleDashboardのデフォルト値はゼロとnullと空リスト`() {
        // Arrange（準備）
        val dashboard = VehicleDashboard()

        // Act & Assert（実行・検証）
        assertEquals(0, dashboard.monthlyCost)
        assertEquals(0, dashboard.yearlyCost)
        assertEquals(0, dashboard.totalCost)
        assertNull(dashboard.averageMonthlyCost)
        assertNull(dashboard.averageFuelEconomy)
        assertEquals(0, dashboard.totalDistance)
        assertNull(dashboard.costPerKm)
        assertNull(dashboard.nextMaintenanceLabel)
        assertNull(dashboard.nextMaintenanceDays)
        assertEquals(emptyList<Pair<CostCategory, Int>>(), dashboard.costByCategory)
        assertEquals(emptyList<Pair<String, Float>>(), dashboard.monthlyCostTrend)
        assertEquals(emptyList<Pair<String, Float>>(), dashboard.fuelEconomyTrend)
        assertEquals(emptyList<MonthlyCostSlice>(), dashboard.monthlyCostByCategory)
    }
}
