package com.segnities007.stylish_myvehicles.domain.service

import com.segnities007.stylish_myvehicles.domain.model.VehicleCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class VehicleCategoryTaxCalculatorTest {

    // ── 乗用車（排気量基準・既存ブラケット） ──
    @Test
    fun `car tax follows displacement brackets`() {
        assertEquals(25_000, VehicleTaxCalculator.calculateTax(VehicleCategory.CAR, 1000))
        assertEquals(36_000, VehicleTaxCalculator.calculateTax(VehicleCategory.CAR, 2000))
        assertEquals(110_000, VehicleTaxCalculator.calculateTax(VehicleCategory.CAR, 7000))
    }

    @Test
    fun `car tax is null without displacement`() {
        assertNull(VehicleTaxCalculator.calculateTax(VehicleCategory.CAR, null))
    }

    // ── 軽自動車（一律 10,800円） ──
    @Test
    fun `kei car tax is flat 10800 regardless of displacement`() {
        assertEquals(10_800, VehicleTaxCalculator.calculateTax(VehicleCategory.KEI_CAR, 660))
        assertEquals(10_800, VehicleTaxCalculator.calculateTax(VehicleCategory.KEI_CAR, null))
    }

    // ── バイク（軽自動車税 種別割・排気量区分） ──
    @Test
    fun `motorcycle tax 50cc is 2000`() {
        assertEquals(2_000, VehicleTaxCalculator.calculateTax(VehicleCategory.MOTORCYCLE, 50))
    }

    @Test
    fun `motorcycle tax 90cc is 2000`() {
        assertEquals(2_000, VehicleTaxCalculator.calculateTax(VehicleCategory.MOTORCYCLE, 90))
    }

    @Test
    fun `motorcycle tax 125cc is 2400`() {
        assertEquals(2_400, VehicleTaxCalculator.calculateTax(VehicleCategory.MOTORCYCLE, 125))
    }

    @Test
    fun `motorcycle tax 250cc is 3600`() {
        assertEquals(3_600, VehicleTaxCalculator.calculateTax(VehicleCategory.MOTORCYCLE, 250))
    }

    @Test
    fun `motorcycle tax over 250cc is 6000`() {
        assertEquals(6_000, VehicleTaxCalculator.calculateTax(VehicleCategory.MOTORCYCLE, 400))
        assertEquals(6_000, VehicleTaxCalculator.calculateTax(VehicleCategory.MOTORCYCLE, 1000))
    }

    @Test
    fun `motorcycle tax boundary values`() {
        assertEquals(2_000, VehicleTaxCalculator.calculateTax(VehicleCategory.MOTORCYCLE, 50))
        assertEquals(2_000, VehicleTaxCalculator.calculateTax(VehicleCategory.MOTORCYCLE, 51))
        assertEquals(2_400, VehicleTaxCalculator.calculateTax(VehicleCategory.MOTORCYCLE, 91))
        assertEquals(2_400, VehicleTaxCalculator.calculateTax(VehicleCategory.MOTORCYCLE, 125))
        assertEquals(3_600, VehicleTaxCalculator.calculateTax(VehicleCategory.MOTORCYCLE, 126))
        assertEquals(3_600, VehicleTaxCalculator.calculateTax(VehicleCategory.MOTORCYCLE, 250))
        assertEquals(6_000, VehicleTaxCalculator.calculateTax(VehicleCategory.MOTORCYCLE, 251))
    }

    @Test
    fun `motorcycle tax is null without displacement`() {
        assertNull(VehicleTaxCalculator.calculateTax(VehicleCategory.MOTORCYCLE, null))
    }

    // ── 自転車（税金なし） ──
    @Test
    fun `bicycle has no tax`() {
        assertNull(VehicleTaxCalculator.calculateTax(VehicleCategory.BICYCLE, null))
        assertNull(VehicleTaxCalculator.calculateTax(VehicleCategory.BICYCLE, 0))
    }

    // ── トラック（最大積載量基準） ──
    @Test
    fun `truck tax follows load capacity brackets`() {
        assertEquals(8_000, VehicleTaxCalculator.calculateTax(VehicleCategory.TRUCK, null, 1000))
        assertEquals(11_500, VehicleTaxCalculator.calculateTax(VehicleCategory.TRUCK, null, 2000))
        assertEquals(16_000, VehicleTaxCalculator.calculateTax(VehicleCategory.TRUCK, null, 3000))
        assertEquals(20_500, VehicleTaxCalculator.calculateTax(VehicleCategory.TRUCK, null, 4000))
        assertEquals(25_500, VehicleTaxCalculator.calculateTax(VehicleCategory.TRUCK, null, 5000))
        assertEquals(30_500, VehicleTaxCalculator.calculateTax(VehicleCategory.TRUCK, null, 6000))
        assertEquals(35_500, VehicleTaxCalculator.calculateTax(VehicleCategory.TRUCK, null, 7000))
        assertEquals(40_500, VehicleTaxCalculator.calculateTax(VehicleCategory.TRUCK, null, 8000))
        assertEquals(45_500, VehicleTaxCalculator.calculateTax(VehicleCategory.TRUCK, null, 9000))
    }

    @Test
    fun `truck tax is null without load capacity`() {
        assertNull(VehicleTaxCalculator.calculateTax(VehicleCategory.TRUCK, null, null))
    }

    // ── その他（税額不明） ──
    @Test
    fun `other category has no tax`() {
        assertNull(VehicleTaxCalculator.calculateTax(VehicleCategory.OTHER, null))
    }
}
