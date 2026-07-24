package com.segnities007.stylish_myvehicles.presentation.screen.records

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class PeriodTest {

    private val now = LocalDate.of(2026, 7, 24)

    // ─── computePeriods: MONTHLY ───────────────────────────────────────

    @Test
    fun `月次モードで記録がない場合でも前月と今月の2ページが生成される`() {
        // Arrange（準備）
        // Act（実行）
        val periods = computePeriods(PeriodMode.MONTHLY, emptyList(), now)

        // Assert（検証）
        assertEquals(2, periods.size)
        assertEquals("2026年6月", periods[0].label)
        assertEquals("2026年7月", periods[1].label)
        assertEquals(LocalDate.of(2026, 6, 1), periods[0].start)
        assertEquals(LocalDate.of(2026, 6, 30), periods[0].endInclusive)
        assertEquals(LocalDate.of(2026, 7, 1), periods[1].start)
        assertEquals(LocalDate.of(2026, 7, 31), periods[1].endInclusive)
    }

    @Test
    fun `月次モードで古い記録がある場合はその月から今月まで連続したページが生成される`() {
        // Arrange（準備）
        val dates = listOf(
            LocalDate.of(2026, 4, 15),
            LocalDate.of(2026, 7, 10),
        )

        // Act（実行）
        val periods = computePeriods(PeriodMode.MONTHLY, dates, now)

        // Assert（検証）
        assertEquals(4, periods.size)
        assertEquals("2026年4月", periods.first().label)
        assertEquals("2026年7月", periods.last().label)
    }

    @Test
    fun `月次モードで将来の記録がある場合はその月までページが生成される`() {
        // Arrange（準備）
        val dates = listOf(LocalDate.of(2026, 9, 1))

        // Act（実行）
        val periods = computePeriods(PeriodMode.MONTHLY, dates, now)

        // Assert（検証）
        assertEquals("2026年6月", periods.first().label)
        assertEquals("2026年9月", periods.last().label)
        assertEquals(4, periods.size)
    }

    // ─── computePeriods: YEARLY ────────────────────────────────────────

    @Test
    fun `年次モードで記録がない場合でも前年と今年の2ページが生成される`() {
        // Arrange（準備）
        // Act（実行）
        val periods = computePeriods(PeriodMode.YEARLY, emptyList(), now)

        // Assert（検証）
        assertEquals(2, periods.size)
        assertEquals("2025年", periods[0].label)
        assertEquals("2026年", periods[1].label)
        assertEquals(LocalDate.of(2025, 1, 1), periods[0].start)
        assertEquals(LocalDate.of(2025, 12, 31), periods[0].endInclusive)
    }

    @Test
    fun `年次モードで古い記録がある場合はその年から今年までページが生成される`() {
        // Arrange（準備）
        val dates = listOf(
            LocalDate.of(2023, 5, 1),
            LocalDate.of(2026, 7, 10),
        )

        // Act（実行）
        val periods = computePeriods(PeriodMode.YEARLY, dates, now)

        // Assert（検証）
        assertEquals(4, periods.size)
        assertEquals("2023年", periods.first().label)
        assertEquals("2026年", periods.last().label)
    }

    // ─── computePeriods: WEEKLY ────────────────────────────────────────

    @Test
    fun `週次モードで記録がない場合でも前週と今週の2ページが生成される`() {
        // Arrange（準備）
        // 2026-07-24 is Friday. Current Monday = 2026-07-20
        // Act（実行）
        val periods = computePeriods(PeriodMode.WEEKLY, emptyList(), now)

        // Assert（検証）
        assertEquals(2, periods.size)
        // Previous week Monday = 2026-07-13
        assertEquals(LocalDate.of(2026, 7, 13), periods[0].start)
        assertEquals(LocalDate.of(2026, 7, 19), periods[0].endInclusive)
        // Current week Monday = 2026-07-20
        assertEquals(LocalDate.of(2026, 7, 20), periods[1].start)
        assertEquals(LocalDate.of(2026, 7, 26), periods[1].endInclusive)
    }

    @Test
    fun `週次モードで古い記録がある場合はその週から今週までページが生成される`() {
        // Arrange（準備）
        val dates = listOf(LocalDate.of(2026, 6, 29)) // Monday of a week 3 weeks ago

        // Act（実行）
        val periods = computePeriods(PeriodMode.WEEKLY, dates, now)

        // Assert（検証）
        assertTrue(periods.size >= 3)
        assertEquals(LocalDate.of(2026, 6, 29), periods.first().start)
        assertEquals(LocalDate.of(2026, 7, 20), periods.last().start)
    }

    @Test
    fun `週次モードのラベルは月日範囲形式である`() {
        // Arrange（準備）
        // Act（実行）
        val periods = computePeriods(PeriodMode.WEEKLY, emptyList(), now)

        // Assert（検証）
        assertEquals("7/20〜7/26", periods.last().label)
    }

    // ─── computePeriods: ALL ───────────────────────────────────────────

    @Test
    fun `すべてモードでは単一のページが生成される`() {
        // Arrange（準備）
        val dates = listOf(LocalDate.of(2025, 1, 15))

        // Act（実行）
        val periods = computePeriods(PeriodMode.ALL, dates, now)

        // Assert（検証）
        assertEquals(1, periods.size)
        assertEquals("すべての期間", periods[0].label)
        assertEquals(LocalDate.of(2025, 1, 15), periods[0].start)
        assertEquals(now, periods[0].endInclusive)
    }

    @Test
    fun `すべてモードで記録がない場合は今日が開始日になる`() {
        // Arrange（準備）
        // Act（実行）
        val periods = computePeriods(PeriodMode.ALL, emptyList(), now)

        // Assert（検証）
        assertEquals(1, periods.size)
        assertEquals(now, periods[0].start)
        assertEquals(now, periods[0].endInclusive)
    }

    // ─── subPeriods: MONTHLY ───────────────────────────────────────────

    @Test
    fun `月次サブ期間はその月で終わる直近6か月を返す`() {
        // Arrange（準備）
        val period = Period("2026年7月", LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 31))

        // Act（実行）
        val subs = subPeriods(period, PeriodMode.MONTHLY)

        // Assert（検証）
        assertEquals(6, subs.size)
        assertEquals("2月", subs.first().label)
        assertEquals("7月", subs.last().label)
        assertEquals(LocalDate.of(2026, 2, 1), subs.first().start)
        assertEquals(LocalDate.of(2026, 2, 28), subs.first().endInclusive)
        assertEquals(LocalDate.of(2026, 7, 31), subs.last().endInclusive)
    }

    // ─── subPeriods: YEARLY ────────────────────────────────────────────

    @Test
    fun `年次サブ期間は12か月を返す`() {
        // Arrange（準備）
        val period = Period("2026年", LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31))

        // Act（実行）
        val subs = subPeriods(period, PeriodMode.YEARLY)

        // Assert（検証）
        assertEquals(12, subs.size)
        assertEquals("1月", subs.first().label)
        assertEquals("12月", subs.last().label)
        assertEquals(LocalDate.of(2026, 1, 1), subs.first().start)
        assertEquals(LocalDate.of(2026, 12, 31), subs.last().endInclusive)
    }

    // ─── subPeriods: WEEKLY ────────────────────────────────────────────

    @Test
    fun `週次サブ期間は7日分を返す`() {
        // Arrange（準備）
        val period = Period("7/20〜7/26", LocalDate.of(2026, 7, 20), LocalDate.of(2026, 7, 26))

        // Act（実行）
        val subs = subPeriods(period, PeriodMode.WEEKLY)

        // Assert（検証）
        assertEquals(7, subs.size)
        assertEquals("20日", subs.first().label)
        assertEquals("26日", subs.last().label)
        assertEquals(LocalDate.of(2026, 7, 20), subs.first().start)
        assertEquals(LocalDate.of(2026, 7, 20), subs.first().endInclusive)
    }

    // ─── subPeriods: ALL ───────────────────────────────────────────────

    @Test
    fun `すべてモードのサブ期間は当月で終わる直近6か月を返す`() {
        // Arrange（準備）
        val period = Period("すべての期間", LocalDate.of(2025, 1, 1), now)

        // Act（実行）
        val subs = subPeriods(period, PeriodMode.ALL)

        // Assert（検証）
        assertEquals(6, subs.size)
        // YearMonth.now() に依存するが、最後は当月
        assertEquals("${java.time.YearMonth.now().monthValue}月", subs.last().label)
    }
}
