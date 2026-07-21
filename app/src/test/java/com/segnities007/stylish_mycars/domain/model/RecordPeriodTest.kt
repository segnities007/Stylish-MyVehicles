package com.segnities007.stylish_mycars.domain.model

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class RecordPeriodTest {

    private val today = LocalDate.of(2026, 7, 20)

    @Test
    fun `start date is computed per period`() {
        assertEquals(today.minusMonths(1), RecordPeriod.MONTH_1.startDate(today))
        assertEquals(today.minusMonths(3), RecordPeriod.MONTH_3.startDate(today))
        assertEquals(today.minusMonths(6), RecordPeriod.MONTH_6.startDate(today))
        assertEquals(today.minusYears(1), RecordPeriod.YEAR_1.startDate(today))
    }

    @Test
    fun `ALL period has no start date`() {
        assertNull(RecordPeriod.ALL.startDate(today))
    }

    @Test
    fun `filter keeps records within the period`() {
        val records = listOf(
            LocalDate.of(2026, 7, 10), // within 1 month
            LocalDate.of(2026, 5, 1), // older than 1 month
            LocalDate.of(2026, 7, 19), // within
        )
        val filtered = RecordPeriod.MONTH_1.filter(records, today) { it }
        assertEquals(
            listOf(LocalDate.of(2026, 7, 10), LocalDate.of(2026, 7, 19)),
            filtered,
        )
    }

    @Test
    fun `filter with ALL returns everything`() {
        val records = listOf(
            LocalDate.of(2020, 1, 1),
            LocalDate.of(2026, 7, 19),
        )
        assertEquals(records, RecordPeriod.ALL.filter(records, today) { it })
    }

    @Test
    fun `filter boundary includes the exact start date`() {
        val start = today.minusMonths(3)
        val records = listOf(start, start.minusDays(1))
        val filtered = RecordPeriod.MONTH_3.filter(records, today) { it }
        assertEquals(listOf(start), filtered)
    }
}
