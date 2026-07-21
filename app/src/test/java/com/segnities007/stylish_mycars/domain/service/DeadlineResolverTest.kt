package com.segnities007.stylish_mycars.domain.service

import com.segnities007.stylish_mycars.domain.model.Vehicle
import com.segnities007.stylish_mycars.domain.model.VehicleCategory
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DeadlineResolverTest {

    private val today = LocalDate.of(2026, 7, 20)

    @Test
    fun `returns null when vehicle has no deadlines`() {
        val vehicle = Vehicle(maker = "トヨタ", name = "プリウス", category = VehicleCategory.BICYCLE)
        assertNull(DeadlineResolver.resolve(vehicle, today))
    }

    @Test
    fun `returns inspection deadline when it is the only one`() {
        val vehicle = Vehicle(
            maker = "トヨタ", name = "プリウス",
            firstRegistrationDate = LocalDate.of(2024, 1, 1), // 車検 2027-01-01
        )
        val result = DeadlineResolver.resolve(vehicle, today)!!
        assertEquals("車検", result.label)
        assertEquals(LocalDate.of(2027, 1, 1), result.date)
    }

    @Test
    fun `returns the most urgent deadline among several`() {
        val vehicle = Vehicle(
            maker = "トヨタ", name = "プリウス",
            firstRegistrationDate = LocalDate.of(2024, 1, 1), // 車検 2027-01-01 (先)
            insuranceExpiry = LocalDate.of(2026, 9, 1), // 任意保険 (手前・最も緊急)
        )
        val result = DeadlineResolver.resolve(vehicle, today)!!
        assertEquals("任意保険", result.label)
    }

    @Test
    fun `expired deadline is the most urgent`() {
        val vehicle = Vehicle(
            maker = "トヨタ", name = "プリウス",
            jibaiExpiry = LocalDate.of(2026, 7, 1), // 期限切れ
            insuranceExpiry = LocalDate.of(2026, 12, 1),
        )
        val result = DeadlineResolver.resolve(vehicle, today)!!
        assertEquals("自賠責保険", result.label)
        assertTrue(result.isExpired)
    }

    @Test
    fun `bicycle without inspection still resolves insurance`() {
        val vehicle = Vehicle(
            maker = "ブリヂストン", name = "自転車",
            category = VehicleCategory.BICYCLE,
            insuranceExpiry = LocalDate.of(2026, 10, 1),
        )
        val result = DeadlineResolver.resolve(vehicle, today)!!
        assertEquals("任意保険", result.label)
    }

    @Test
    fun `isUrgent is true within 30 days`() {
        val vehicle = Vehicle(
            maker = "トヨタ", name = "プリウス",
            insuranceExpiry = today.plusDays(10),
        )
        val result = DeadlineResolver.resolve(vehicle, today)!!
        assertTrue(result.isUrgent)
    }
}
