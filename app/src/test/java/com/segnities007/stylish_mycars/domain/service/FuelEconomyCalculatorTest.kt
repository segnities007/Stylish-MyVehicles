package com.segnities007.stylish_mycars.domain.service

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class FuelEconomyCalculatorTest {
    @Test
    fun `calculate returns correct fuel economy`() {
        val result = FuelEconomyCalculator.calculate(
            currentOdometer = 10500,
            previousOdometer = 10000,
            volume = 25.0,
        )
        assertEquals(20.0, result!!, 0.001)
    }

    @Test
    fun `calculate returns null when volume is zero`() {
        val result = FuelEconomyCalculator.calculate(
            currentOdometer = 10500,
            previousOdometer = 10000,
            volume = 0.0,
        )
        assertNull(result)
    }

    @Test
    fun `calculate returns null when volume is negative`() {
        val result = FuelEconomyCalculator.calculate(
            currentOdometer = 10500,
            previousOdometer = 10000,
            volume = -5.0,
        )
        assertNull(result)
    }

    @Test
    fun `calculate returns null when odometer did not increase`() {
        val result = FuelEconomyCalculator.calculate(
            currentOdometer = 10000,
            previousOdometer = 10000,
            volume = 25.0,
        )
        assertNull(result)
    }

    @Test
    fun `calculate returns null when current odometer is less than previous`() {
        val result = FuelEconomyCalculator.calculate(
            currentOdometer = 9000,
            previousOdometer = 10000,
            volume = 25.0,
        )
        assertNull(result)
    }
}
