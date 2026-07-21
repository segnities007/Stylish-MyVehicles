package com.segnities007.stylish_mycars.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MaintenanceScheduleDefaultsTest {

    private fun categoriesOf(schedules: List<MaintenanceSchedule>): Set<MaintenanceCategory> =
        schedules.map { it.category }.toSet()

    @Test
    fun `car defaults include oil and inspection`() {
        val defaults = MaintenanceSchedule.defaults(1, VehicleCategory.CAR)
        val categories = categoriesOf(defaults)
        assertTrue(categories.contains(MaintenanceCategory.OIL))
        assertTrue(categories.contains(MaintenanceCategory.OIL_FILTER))
        assertTrue(categories.contains(MaintenanceCategory.INSPECTION_12))
        // 車のオイルは 5,000km / 6ヶ月
        val oil = defaults.first { it.category == MaintenanceCategory.OIL }
        assertEquals(5000, oil.intervalKm)
        assertEquals(6, oil.intervalMonths)
    }

    @Test
    fun `motorcycle defaults include chain and shorter oil interval`() {
        val defaults = MaintenanceSchedule.defaults(1, VehicleCategory.MOTORCYCLE)
        val categories = categoriesOf(defaults)
        assertTrue(categories.contains(MaintenanceCategory.CHAIN))
        assertTrue(categories.contains(MaintenanceCategory.OIL))
        // バイクのオイルは 3,000km / 6ヶ月
        val oil = defaults.first { it.category == MaintenanceCategory.OIL }
        assertEquals(3000, oil.intervalKm)
        // チェーンは 1,000km / 1ヶ月
        val chain = defaults.first { it.category == MaintenanceCategory.CHAIN }
        assertEquals(1000, chain.intervalKm)
        assertEquals(1, chain.intervalMonths)
    }

    @Test
    fun `bicycle defaults have no engine oil but have chain and gear`() {
        val defaults = MaintenanceSchedule.defaults(1, VehicleCategory.BICYCLE)
        val categories = categoriesOf(defaults)
        assertTrue(!categories.contains(MaintenanceCategory.OIL))
        assertTrue(categories.contains(MaintenanceCategory.CHAIN))
        assertTrue(categories.contains(MaintenanceCategory.GEAR))
        assertTrue(categories.contains(MaintenanceCategory.BRAKE))
    }

    @Test
    fun `truck defaults have longer oil interval`() {
        val defaults = MaintenanceSchedule.defaults(1, VehicleCategory.TRUCK)
        val oil = defaults.first { it.category == MaintenanceCategory.OIL }
        assertEquals(10000, oil.intervalKm)
    }

    @Test
    fun `kei car defaults match car defaults`() {
        val kei = MaintenanceSchedule.defaults(1, VehicleCategory.KEI_CAR)
        val car = MaintenanceSchedule.defaults(1, VehicleCategory.CAR)
        assertEquals(categoriesOf(car), categoriesOf(kei))
    }

    @Test
    fun `all defaults carry the vehicle id`() {
        val defaults = MaintenanceSchedule.defaults(42, VehicleCategory.MOTORCYCLE)
        assertTrue(defaults.all { it.vehicleId == 42L })
    }
}
