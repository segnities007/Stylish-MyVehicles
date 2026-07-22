package com.segnities007.stylish_myvehicles.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDate

class MaintenanceScheduleDueDateTest {

    private fun schedule(
        intervalMonths: Int? = 6,
        lastDoneDate: LocalDate? = LocalDate.of(2026, 1, 15),
    ) = MaintenanceSchedule(
        id = 1,
        vehicleId = 1,
        category = MaintenanceCategory.OIL,
        intervalMonths = intervalMonths,
        lastDoneDate = lastDoneDate,
    )

    // --- dueDate ---

    @Test
    fun `dueDate returns lastDone plus interval months`() {
        val s = schedule(intervalMonths = 6, lastDoneDate = LocalDate.of(2026, 1, 15))
        assertEquals(LocalDate.of(2026, 7, 15), s.dueDate)
    }

    @Test
    fun `dueDate is null when intervalMonths is null`() {
        val s = schedule(intervalMonths = null)
        assertNull(s.dueDate)
    }

    @Test
    fun `dueDate is null when lastDoneDate is null`() {
        val s = schedule(lastDoneDate = null)
        assertNull(s.dueDate)
    }

    @Test
    fun `dueDate handles year boundary`() {
        val s = schedule(intervalMonths = 6, lastDoneDate = LocalDate.of(2026, 10, 1))
        assertEquals(LocalDate.of(2027, 4, 1), s.dueDate)
    }

    @Test
    fun `dueDate handles 12 month interval`() {
        val s = schedule(intervalMonths = 12, lastDoneDate = LocalDate.of(2025, 3, 20))
        assertEquals(LocalDate.of(2026, 3, 20), s.dueDate)
    }

    // --- daysUntilDue ---

    @Test
    fun `daysUntilDue returns positive days when due date is in future`() {
        val s = schedule(intervalMonths = 6, lastDoneDate = LocalDate.of(2026, 1, 15))
        // due = 2026-07-15, now = 2026-07-01 => 14 days
        val days = s.daysUntilDue(LocalDate.of(2026, 7, 1))
        assertEquals(14L, days)
    }

    @Test
    fun `daysUntilDue returns zero on due date`() {
        val s = schedule(intervalMonths = 6, lastDoneDate = LocalDate.of(2026, 1, 15))
        // due = 2026-07-15, now = 2026-07-15 => 0 days
        val days = s.daysUntilDue(LocalDate.of(2026, 7, 15))
        assertEquals(0L, days)
    }

    @Test
    fun `daysUntilDue returns negative days when overdue`() {
        val s = schedule(intervalMonths = 6, lastDoneDate = LocalDate.of(2026, 1, 15))
        // due = 2026-07-15, now = 2026-07-20 => -5 days
        val days = s.daysUntilDue(LocalDate.of(2026, 7, 20))
        assertEquals(-5L, days)
    }

    @Test
    fun `daysUntilDue is null when intervalMonths is null`() {
        val s = schedule(intervalMonths = null)
        assertNull(s.daysUntilDue(LocalDate.of(2026, 7, 1)))
    }

    @Test
    fun `daysUntilDue is null when lastDoneDate is null`() {
        val s = schedule(lastDoneDate = null)
        assertNull(s.daysUntilDue(LocalDate.of(2026, 7, 1)))
    }

    @Test
    fun `daysUntilDue handles long interval`() {
        val s = schedule(intervalMonths = 24, lastDoneDate = LocalDate.of(2025, 1, 1))
        // due = 2027-01-01, now = 2026-07-01 => 184 days
        val days = s.daysUntilDue(LocalDate.of(2026, 7, 1))
        assertEquals(184L, days)
    }
}
