package com.segnities007.stylish_mycars.domain.service

object FuelEconomyCalculator {
    fun calculate(
        currentOdometer: Int,
        previousOdometer: Int,
        volume: Double,
    ): Double? {
        if (volume <= 0) return null
        val distance = currentOdometer - previousOdometer
        if (distance <= 0) return null
        return distance / volume
    }
}
