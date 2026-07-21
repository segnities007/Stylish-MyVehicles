package com.segnities007.stylish_myvehicles.domain.service

import com.segnities007.stylish_myvehicles.domain.model.VehicleCategory
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class VehicleCategoryInspectionCalculatorTest {

    private val today = LocalDate.of(2026, 7, 20)

    // ── 車検要否 ──
    @Test
    fun `car and kei car require inspection`() {
        assertTrue(InspectionCalculator.requiresInspection(VehicleCategory.CAR, 1800))
        assertTrue(InspectionCalculator.requiresInspection(VehicleCategory.KEI_CAR, 660))
    }

    @Test
    fun `motorcycle requires inspection only over 250cc`() {
        assertFalse(InspectionCalculator.requiresInspection(VehicleCategory.MOTORCYCLE, 250))
        assertFalse(InspectionCalculator.requiresInspection(VehicleCategory.MOTORCYCLE, 125))
        assertTrue(InspectionCalculator.requiresInspection(VehicleCategory.MOTORCYCLE, 251))
        assertTrue(InspectionCalculator.requiresInspection(VehicleCategory.MOTORCYCLE, 400))
    }

    @Test
    fun `motorcycle without displacement does not require inspection`() {
        assertFalse(InspectionCalculator.requiresInspection(VehicleCategory.MOTORCYCLE, null))
    }

    @Test
    fun `bicycle does not require inspection`() {
        assertFalse(InspectionCalculator.requiresInspection(VehicleCategory.BICYCLE, null))
    }

    @Test
    fun `truck requires inspection`() {
        assertTrue(InspectionCalculator.requiresInspection(VehicleCategory.TRUCK, 3000))
    }

    // ── 初回・継続の年数 ──
    @Test
    fun `car first inspection is 3 years then every 2 years`() {
        assertEquals(3, InspectionCalculator.firstInspectionYears(VehicleCategory.CAR))
        assertEquals(2, InspectionCalculator.inspectionIntervalYears(VehicleCategory.CAR))
    }

    @Test
    fun `truck first inspection is 2 years then every 1 year`() {
        assertEquals(2, InspectionCalculator.firstInspectionYears(VehicleCategory.TRUCK))
        assertEquals(1, InspectionCalculator.inspectionIntervalYears(VehicleCategory.TRUCK))
    }

    // ── 現在基準の満了日 ──
    @Test
    fun `car current expiry advances by 2 years past today`() {
        // 初度登録 2020-04 → 初回2023, 2025, 2027 → today 2026-07 の次は 2027-04
        val firstReg = LocalDate.of(2020, 4, 1)
        assertEquals(
            LocalDate.of(2027, 4, 1),
            InspectionCalculator.calculateCurrentExpiry(VehicleCategory.CAR, firstReg, 1800, today),
        )
    }

    @Test
    fun `truck current expiry advances by 1 year past today`() {
        // 初度登録 2020-04 → 初回2022, 2023, ..., 2026, 2027 → today 2026-07 の次は 2027-04
        val firstReg = LocalDate.of(2020, 4, 1)
        assertEquals(
            LocalDate.of(2027, 4, 1),
            InspectionCalculator.calculateCurrentExpiry(VehicleCategory.TRUCK, firstReg, 3000, today),
        )
    }

    @Test
    fun `bicycle current expiry is null`() {
        assertNull(
            InspectionCalculator.calculateCurrentExpiry(
                VehicleCategory.BICYCLE, LocalDate.of(2020, 4, 1), null, today,
            ),
        )
    }

    @Test
    fun `motorcycle under 250cc current expiry is null`() {
        assertNull(
            InspectionCalculator.calculateCurrentExpiry(
                VehicleCategory.MOTORCYCLE, LocalDate.of(2020, 4, 1), 125, today,
            ),
        )
    }

    @Test
    fun `motorcycle over 250cc current expiry is computed`() {
        val firstReg = LocalDate.of(2020, 4, 1)
        assertEquals(
            LocalDate.of(2027, 4, 1),
            InspectionCalculator.calculateCurrentExpiry(VehicleCategory.MOTORCYCLE, firstReg, 400, today),
        )
    }
}
