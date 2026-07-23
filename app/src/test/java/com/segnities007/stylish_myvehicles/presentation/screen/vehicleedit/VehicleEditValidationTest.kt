package com.segnities007.stylish_myvehicles.presentation.screen.vehicleedit

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import com.segnities007.stylish_myvehicles.presentation.util.normalizeIntegerInput

class VehicleEditValidationTest {
    @Test
    fun `positive numeric weight is valid`() {
        val state = VehicleEditUiState(maker = "トヨタ", name = "プリウス", weight = "1350")

        assertNull(state.weightError)
        assertTrue(state.canSave)
    }

    @Test
    fun `invalid optional numeric field prevents save`() {
        val state = VehicleEditUiState(maker = "トヨタ", name = "プリウス", weight = "0")

        assertFalse(state.canSave)
    }

    @Test
    fun `full width digits are normalized to ascii`() {
        assertEquals("1350", "１３５０".normalizeIntegerInput())
    }

    @Test
    fun `separators and units are safely removed`() {
        assertEquals("1350", "1,350 kg".normalizeIntegerInput())
    }
}
