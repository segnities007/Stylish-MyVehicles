package com.segnities007.stylish_mycars.domain.service

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class InspectionCalculatorTest {
    @Test
    fun `calculateFirstExpiry returns 3 years after first registration`() {
        val firstReg = LocalDate.of(2022, 4, 1)
        val expected = LocalDate.of(2025, 4, 1)
        assertEquals(expected, InspectionCalculator.calculateFirstExpiry(firstReg))
    }

    @Test
    fun `calculateNextExpiry returns 2 years after current expiry`() {
        val currentExpiry = LocalDate.of(2025, 4, 1)
        val expected = LocalDate.of(2027, 4, 1)
        assertEquals(expected, InspectionCalculator.calculateNextExpiry(currentExpiry))
    }

    @Test
    fun `daysUntilExpiry returns positive days for future date`() {
        val expiry = LocalDate.of(2026, 8, 1)
        val today = LocalDate.of(2026, 7, 20)
        assertEquals(12, InspectionCalculator.daysUntilExpiry(expiry, today))
    }

    @Test
    fun `daysUntilExpiry returns negative days for past date`() {
        val expiry = LocalDate.of(2026, 7, 1)
        val today = LocalDate.of(2026, 7, 20)
        assertEquals(-19, InspectionCalculator.daysUntilExpiry(expiry, today))
    }

    @Test
    fun `daysUntilExpiry returns zero for same date`() {
        val date = LocalDate.of(2026, 7, 20)
        assertEquals(0, InspectionCalculator.daysUntilExpiry(date, date))
    }

    @Test
    fun `calculateCurrentExpiry returns first expiry when still in the future`() {
        // 初度登録 2024-04 → 初回満了 2027-04（today 2026 より未来）
        val firstReg = LocalDate.of(2024, 4, 1)
        val today = LocalDate.of(2026, 7, 20)
        assertEquals(
            LocalDate.of(2027, 4, 1),
            InspectionCalculator.calculateCurrentExpiry(firstReg, today),
        )
    }

    @Test
    fun `calculateCurrentExpiry advances by 2 years past today for older cars`() {
        // 初度登録 2020-04 → 2023, 2025, 2027... today 2026-07 の次は 2027-04
        val firstReg = LocalDate.of(2020, 4, 1)
        val today = LocalDate.of(2026, 7, 20)
        assertEquals(
            LocalDate.of(2027, 4, 1),
            InspectionCalculator.calculateCurrentExpiry(firstReg, today),
        )
    }

    @Test
    fun `calculateCurrentExpiry returns a future date even for very old cars`() {
        // 初度登録 2010-04 → 2013,2015,...,2025,2027 → today 2026-07 の次は 2027-04
        val firstReg = LocalDate.of(2010, 4, 1)
        val today = LocalDate.of(2026, 7, 20)
        val result = InspectionCalculator.calculateCurrentExpiry(firstReg, today)
        assertEquals(LocalDate.of(2027, 4, 1), result)
        assert(result.isAfter(today))
    }

    @Test
    fun `calculateCurrentExpiry skips expiry that equals today`() {
        // 満了日が today と同日なら「期限切れ扱い」のため次へ進める
        // 初度登録 2021-07 → 初回 2024-07, 次 2026-07(=today), その次 2028-07
        val firstReg = LocalDate.of(2021, 7, 1)
        val today = LocalDate.of(2026, 7, 1)
        assertEquals(
            LocalDate.of(2028, 7, 1),
            InspectionCalculator.calculateCurrentExpiry(firstReg, today),
        )
    }
}
