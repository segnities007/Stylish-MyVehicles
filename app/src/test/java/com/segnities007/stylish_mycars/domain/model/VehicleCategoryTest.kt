package com.segnities007.stylish_mycars.domain.model

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class VehicleCategoryTest {

    @Test
    fun `motorized vehicles use fuel, bicycle does not`() {
        assertTrue(VehicleCategory.CAR.usesFuel)
        assertTrue(VehicleCategory.KEI_CAR.usesFuel)
        assertTrue(VehicleCategory.MOTORCYCLE.usesFuel)
        assertTrue(VehicleCategory.TRUCK.usesFuel)
        assertFalse(VehicleCategory.BICYCLE.usesFuel)
    }

    @Test
    fun `bicycle does not require compulsory insurance`() {
        assertFalse(VehicleCategory.BICYCLE.requiresInsurance)
        assertTrue(VehicleCategory.CAR.requiresInsurance)
        assertTrue(VehicleCategory.MOTORCYCLE.requiresInsurance)
        assertTrue(VehicleCategory.TRUCK.requiresInsurance)
    }

    @Test
    fun `every category has a non-blank label`() {
        VehicleCategory.entries.forEach {
            assertTrue(it.label.isNotBlank())
        }
    }
}
