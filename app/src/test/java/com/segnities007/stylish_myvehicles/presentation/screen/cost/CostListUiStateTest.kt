package com.segnities007.stylish_myvehicles.presentation.screen.cost

import com.segnities007.stylish_myvehicles.domain.model.CostCategory
import com.segnities007.stylish_myvehicles.domain.model.CostRecord
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class CostListUiStateTest {

    private fun record(
        id: Long = 1,
        date: LocalDate = LocalDate.of(2026, 7, 10),
        amount: Int = 5000,
        category: CostCategory = CostCategory.FUEL,
    ) = CostRecord(
        id = id,
        vehicleId = 1,
        date = date,
        category = category,
        title = "テスト",
        amount = amount,
    )

    @Test
    fun `totalAmount sums all record amounts`() {
        val state = CostListUiState(
            records = listOf(
                record(id = 1, amount = 5000),
                record(id = 2, amount = 3000),
            ),
        )
        assertEquals(8000, state.totalAmount)
    }

    @Test
    fun `totalAmount is zero when records is empty`() {
        val state = CostListUiState(records = emptyList())
        assertEquals(0, state.totalAmount)
    }

    @Test
    fun `totalAmount respects category filter`() {
        val state = CostListUiState(
            records = listOf(
                record(id = 1, category = CostCategory.FUEL, amount = 5000),
                record(id = 2, category = CostCategory.TAX, amount = 36000),
            ),
            selectedCategory = CostCategory.FUEL,
        )
        assertEquals(5000, state.totalAmount)
    }

    @Test
    fun `monthlyTotal sums only current month records`() {
        val now = LocalDate.now()
        val state = CostListUiState(
            records = listOf(
                record(id = 1, date = now.withDayOfMonth(1), amount = 5000),
                record(id = 2, date = now.minusMonths(1).withDayOfMonth(1), amount = 3000),
            ),
        )
        assertEquals(5000, state.monthlyTotal)
    }

    @Test
    fun `previousMonthTotal sums only previous month records`() {
        val now = LocalDate.now()
        val prevMonth = now.minusMonths(1)
        val state = CostListUiState(
            records = listOf(
                record(id = 1, date = now.withDayOfMonth(1), amount = 5000),
                record(id = 2, date = prevMonth.withDayOfMonth(1), amount = 3000),
            ),
        )
        assertEquals(3000, state.previousMonthTotal)
    }

    @Test
    fun `previousMonthTotal is zero when no previous month records`() {
        val now = LocalDate.now()
        val state = CostListUiState(
            records = listOf(record(id = 1, date = now.withDayOfMonth(1), amount = 5000)),
        )
        assertEquals(0, state.previousMonthTotal)
    }

    @Test
    fun `filteredRecords returns all when no category selected`() {
        val state = CostListUiState(
            records = listOf(record(id = 1), record(id = 2)),
            selectedCategory = null,
        )
        assertEquals(2, state.filteredRecords.size)
    }

    @Test
    fun `filteredRecords filters by selected category`() {
        val state = CostListUiState(
            records = listOf(
                record(id = 1, category = CostCategory.FUEL),
                record(id = 2, category = CostCategory.TAX),
            ),
            selectedCategory = CostCategory.TAX,
        )
        assertEquals(1, state.filteredRecords.size)
        assertEquals(2L, state.filteredRecords.first().id)
    }

    @Test
    fun `canSave is true when title and amount are valid`() {
        val state = CostListUiState(inputTitle = "テスト", inputAmount = "5000")
        assertTrue(state.canSave)
    }

    @Test
    fun `canSave is false when title is blank`() {
        val state = CostListUiState(inputTitle = "", inputAmount = "5000")
        assertFalse(state.canSave)
    }

    @Test
    fun `canSave is false when amount is not a number`() {
        val state = CostListUiState(inputTitle = "テスト", inputAmount = "abc")
        assertFalse(state.canSave)
    }

    @Test
    fun `isEditing is true when editingRecordId is set`() {
        val state = CostListUiState(editingRecordId = 5L)
        assertTrue(state.isEditing)
    }

    @Test
    fun `isEditing is false when editingRecordId is null`() {
        val state = CostListUiState(editingRecordId = null)
        assertFalse(state.isEditing)
    }
}
