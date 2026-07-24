package com.segnities007.stylish_myvehicles.presentation.screen.recordslist

import com.segnities007.stylish_myvehicles.domain.model.CostCategory
import com.segnities007.stylish_myvehicles.domain.model.CostRecord
import com.segnities007.stylish_myvehicles.domain.model.FuelRecord
import com.segnities007.stylish_myvehicles.domain.model.MaintenanceCategory
import com.segnities007.stylish_myvehicles.domain.model.MaintenanceRecord
import com.segnities007.stylish_myvehicles.domain.model.TripRecord
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth

class RecordsListUiStateTest {

    private val date = LocalDate.of(2026, 7, 10)

    private fun fuelEntry(id: Long = 1, amount: Int = 5000) = FuelEntry(
        record = FuelRecord(
            id = id, vehicleId = 1, date = date,
            odometer = 10000, volume = 30.0, amount = amount,
        ),
        vehicleName = "プリウス",
    )

    private fun maintenanceEntry(id: Long = 1, cost: Int = 3000) = MaintenanceEntry(
        record = MaintenanceRecord(
            id = id, vehicleId = 1, date = date,
            category = MaintenanceCategory.OIL, title = "オイル交換", cost = cost,
        ),
        vehicleName = "プリウス",
    )

    private fun costEntry(id: Long = 1, amount: Int = 8000) = CostEntry(
        record = CostRecord(
            id = id, vehicleId = 1, date = date,
            category = CostCategory.PARKING, title = "駐車場", amount = amount,
        ),
        vehicleName = "プリウス",
    )

    private fun tripEntry(id: Long = 1) = TripEntry(
        record = TripRecord(
            id = id, vehicleId = 1,
            startedAt = LocalDateTime.of(2026, 7, 10, 8, 0),
        ),
        vehicleName = "プリウス",
    )

    // ─── RecordEntry 委譲 ──────────────────────────────────────────────

    @Test
    fun `FuelEntryはrecordのidとdateを委譲する`() {
        // Arrange（準備）
        val entry = fuelEntry(id = 42)

        // Act & Assert（実行・検証）
        assertEquals(42L, entry.id)
        assertEquals(date, entry.date)
        assertEquals("プリウス", entry.vehicleName)
    }

    @Test
    fun `MaintenanceEntryはrecordのidとdateを委譲する`() {
        // Arrange（準備）
        val entry = maintenanceEntry(id = 7)

        // Act & Assert（実行・検証）
        assertEquals(7L, entry.id)
        assertEquals(date, entry.date)
    }

    @Test
    fun `CostEntryはrecordのidとdateを委譲する`() {
        // Arrange（準備）
        val entry = costEntry(id = 3)

        // Act & Assert（実行・検証）
        assertEquals(3L, entry.id)
        assertEquals(date, entry.date)
    }

    @Test
    fun `TripEntryはstartedAtの日付をdateとして返す`() {
        // Arrange（準備）
        val entry = tripEntry(id = 5)

        // Act & Assert（実行・検証）
        assertEquals(5L, entry.id)
        assertEquals(LocalDate.of(2026, 7, 10), entry.date)
    }

    // ─── RecordSection.totalAmount ─────────────────────────────────────

    @Test
    fun `totalAmountは全エントリの金額を合算する`() {
        // Arrange（準備）
        val section = RecordSection(
            month = YearMonth.of(2026, 7),
            entries = listOf(
                fuelEntry(amount = 5000),
                maintenanceEntry(cost = 3000),
                costEntry(amount = 8000),
            ),
        )

        // Act（実行）
        val total = section.totalAmount

        // Assert（検証）
        assertEquals(16000, total)
    }

    @Test
    fun `TripEntryはtotalAmountに0として寄与する`() {
        // Arrange（準備）
        val section = RecordSection(
            month = YearMonth.of(2026, 7),
            entries = listOf(tripEntry(), fuelEntry(amount = 5000)),
        )

        // Act（実行）
        val total = section.totalAmount

        // Assert（検証）
        assertEquals(5000, total)
    }

    @Test
    fun `エントリが空の場合totalAmountは0`() {
        // Arrange（準備）
        val section = RecordSection(month = YearMonth.of(2026, 7), entries = emptyList())

        // Act & Assert（実行・検証）
        assertEquals(0, section.totalAmount)
    }

    // ─── RecordsListUiState.totalCount ─────────────────────────────────

    @Test
    fun `totalCountは全セクションのエントリ数を合算する`() {
        // Arrange（準備）
        val state = RecordsListUiState(
            isLoading = false,
            sections = listOf(
                RecordSection(
                    month = YearMonth.of(2026, 7),
                    entries = listOf(fuelEntry(id = 1), costEntry(id = 2)),
                ),
                RecordSection(
                    month = YearMonth.of(2026, 6),
                    entries = listOf(maintenanceEntry(id = 3)),
                ),
            ),
        )

        // Act & Assert（実行・検証）
        assertEquals(3, state.totalCount)
    }

    @Test
    fun `セクションが空の場合totalCountは0`() {
        // Arrange（準備）
        val state = RecordsListUiState(isLoading = false, sections = emptyList())

        // Act & Assert（実行・検証）
        assertEquals(0, state.totalCount)
    }
}
