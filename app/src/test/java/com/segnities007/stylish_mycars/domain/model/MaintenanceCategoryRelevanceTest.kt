package com.segnities007.stylish_mycars.domain.model

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MaintenanceCategoryRelevanceTest {

    @Test
    fun `car relevant categories include oil and wiper but not chain or gear`() {
        val relevant = MaintenanceCategory.relevantFor(VehicleCategory.CAR)
        assertTrue(relevant.contains(MaintenanceCategory.OIL))
        assertTrue(relevant.contains(MaintenanceCategory.WIPER))
        assertTrue(relevant.contains(MaintenanceCategory.AIR_FILTER))
        assertFalse(relevant.contains(MaintenanceCategory.CHAIN))
        assertFalse(relevant.contains(MaintenanceCategory.GEAR))
    }

    @Test
    fun `motorcycle relevant categories include chain but not wiper`() {
        val relevant = MaintenanceCategory.relevantFor(VehicleCategory.MOTORCYCLE)
        assertTrue(relevant.contains(MaintenanceCategory.CHAIN))
        assertTrue(relevant.contains(MaintenanceCategory.OIL))
        assertFalse(relevant.contains(MaintenanceCategory.WIPER))
        assertFalse(relevant.contains(MaintenanceCategory.AIR_FILTER))
    }

    @Test
    fun `bicycle relevant categories have no engine items`() {
        val relevant = MaintenanceCategory.relevantFor(VehicleCategory.BICYCLE)
        assertTrue(relevant.contains(MaintenanceCategory.CHAIN))
        assertTrue(relevant.contains(MaintenanceCategory.GEAR))
        assertTrue(relevant.contains(MaintenanceCategory.BRAKE))
        assertFalse(relevant.contains(MaintenanceCategory.OIL))
        assertFalse(relevant.contains(MaintenanceCategory.SPARK_PLUG))
        assertFalse(relevant.contains(MaintenanceCategory.SHAKEN))
    }

    @Test
    fun `every relevant list ends with OTHER as a catch-all`() {
        VehicleCategory.entries.forEach { category ->
            val relevant = MaintenanceCategory.relevantFor(category)
            assertTrue(relevant.contains(MaintenanceCategory.OTHER))
        }
    }

    @Test
    fun `truck relevant categories include oil but not chain`() {
        val relevant = MaintenanceCategory.relevantFor(VehicleCategory.TRUCK)
        assertTrue(relevant.contains(MaintenanceCategory.OIL))
        assertFalse(relevant.contains(MaintenanceCategory.CHAIN))
    }
}
