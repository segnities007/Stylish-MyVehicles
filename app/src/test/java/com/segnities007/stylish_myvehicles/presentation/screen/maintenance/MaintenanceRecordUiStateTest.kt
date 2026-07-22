package com.segnities007.stylish_myvehicles.presentation.screen.maintenance

import com.segnities007.stylish_myvehicles.domain.model.MaintenanceCategory
import com.segnities007.stylish_myvehicles.domain.model.MaintenanceRecord
import com.segnities007.stylish_myvehicles.domain.model.RecordPeriod
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class MaintenanceRecordUiStateTest {

    private fun record(
        id: Long = 1,
        date: LocalDate = LocalDate.of(2026, 7, 10),
        cost: Int = 5500,
    ) = MaintenanceRecord(
        id = id,
        vehicleId = 1,
        date = date,
        category = MaintenanceCategory.OIL,
        title = "オイル交換",
        cost = cost,
    )

    @Test
    fun `filteredTotalCost sums costs of filtered records`() {
        val state = MaintenanceRecordUiState(
            records = listOf(
                record(id = 1, cost = 5000),
                record(id = 2, cost = 8000),
            ),
            selectedPeriod = RecordPeriod.ALL,
        )
        assertEquals(13000, state.filteredTotalCost)
    }

    @Test
    fun `filteredTotalCost is zero when records is empty`() {
        val state = MaintenanceRecordUiState(records = emptyList())
        assertEquals(0, state.filteredTotalCost)
    }

    @Test
    fun `filteredCount returns number of filtered records`() {
        val state = MaintenanceRecordUiState(
            records = listOf(record(id = 1), record(id = 2), record(id = 3)),
            selectedPeriod = RecordPeriod.ALL,
        )
        assertEquals(3, state.filteredCount)
    }

    @Test
    fun `filteredCount is zero when records is empty`() {
        val state = MaintenanceRecordUiState(records = emptyList())
        assertEquals(0, state.filteredCount)
    }

    @Test
    fun `filteredCount respects period filter`() {
        val now = LocalDate.now()
        val state = MaintenanceRecordUiState(
            records = listOf(
                record(id = 1, date = now.minusDays(3)),
                record(id = 2, date = now.minusMonths(4)),
            ),
            selectedPeriod = RecordPeriod.MONTH_1,
        )
        assertEquals(1, state.filteredCount)
    }

    @Test
    fun `filteredTotalCost respects period filter`() {
        val now = LocalDate.now()
        val state = MaintenanceRecordUiState(
            records = listOf(
                record(id = 1, date = now.minusDays(3), cost = 5000),
                record(id = 2, date = now.minusMonths(4), cost = 8000),
            ),
            selectedPeriod = RecordPeriod.MONTH_1,
        )
        assertEquals(5000, state.filteredTotalCost)
    }
}
