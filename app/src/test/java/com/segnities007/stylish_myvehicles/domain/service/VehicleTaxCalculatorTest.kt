package com.segnities007.stylish_myvehicles.domain.service

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class VehicleTaxCalculatorTest {
    @Test
    fun `returns null for null displacement`() {
        assertNull(VehicleTaxCalculator.calculateTax(null))
    }

    @Test
    fun `returns kei tax for 660cc`() {
        assertEquals(10_800, VehicleTaxCalculator.calculateTax(660))
    }

    @Test
    fun `returns 25000 for 1000cc`() {
        assertEquals(25_000, VehicleTaxCalculator.calculateTax(1000))
    }

    @Test
    fun `returns 30500 for 1500cc`() {
        assertEquals(30_500, VehicleTaxCalculator.calculateTax(1500))
    }

    @Test
    fun `returns 36000 for 2000cc`() {
        assertEquals(36_000, VehicleTaxCalculator.calculateTax(2000))
    }

    @Test
    fun `returns 43500 for 2500cc`() {
        assertEquals(43_500, VehicleTaxCalculator.calculateTax(2500))
    }

    @Test
    fun `returns 50000 for 3000cc`() {
        assertEquals(50_000, VehicleTaxCalculator.calculateTax(3000))
    }

    @Test
    fun `returns 110000 for over 6000cc`() {
        assertEquals(110_000, VehicleTaxCalculator.calculateTax(7000))
    }

    @Test
    fun `returns correct tax for boundary values`() {
        assertEquals(25_000, VehicleTaxCalculator.calculateTax(999))
        assertEquals(25_000, VehicleTaxCalculator.calculateTax(1000))
        assertEquals(30_500, VehicleTaxCalculator.calculateTax(1001))
    }
}
