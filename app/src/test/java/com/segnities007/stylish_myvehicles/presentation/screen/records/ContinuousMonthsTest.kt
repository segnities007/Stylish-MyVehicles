package com.segnities007.stylish_myvehicles.presentation.screen.records

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.YearMonth

class ContinuousMonthsTest {

    private val now = YearMonth.of(2026, 7)

    @Test
    fun `includes previous month even when there are no records`() {
        assertEquals(
            listOf(YearMonth.of(2026, 6), YearMonth.of(2026, 7)),
            continuousMonths(emptyList(), now),
        )
    }

    @Test
    fun `includes previous month when records exist only in current month`() {
        assertEquals(
            listOf(YearMonth.of(2026, 6), YearMonth.of(2026, 7)),
            continuousMonths(listOf(now), now),
        )
    }

    @Test
    fun `fills every month between oldest record and current month in ascending order`() {
        val oldest = YearMonth.of(2026, 4)
        val result = continuousMonths(listOf(now, oldest), now)
        assertEquals(
            listOf(
                YearMonth.of(2026, 4),
                YearMonth.of(2026, 5),
                YearMonth.of(2026, 6),
                YearMonth.of(2026, 7),
            ),
            result,
        )
    }

    @Test
    fun `is sorted ascending and bounded by oldest record`() {
        val result = continuousMonths(
            listOf(YearMonth.of(2026, 5), YearMonth.of(2026, 3), YearMonth.of(2026, 5)),
            now,
        )
        assertEquals(YearMonth.of(2026, 3), result.first())
        assertEquals(YearMonth.of(2026, 7), result.last())
        assertEquals(5, result.size)
    }

    @Test
    fun `includes future record month and spans down to previous month`() {
        val future = YearMonth.of(2026, 9)
        val result = continuousMonths(listOf(future), now)
        assertEquals(YearMonth.of(2026, 6), result.first())
        assertEquals(future, result.last())
        assertEquals(4, result.size)
    }
}
