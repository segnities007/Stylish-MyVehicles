package com.segnities007.stylish_myvehicles.presentation.screen.fuel

import com.segnities007.stylish_myvehicles.domain.model.FuelRecord
import com.segnities007.stylish_myvehicles.domain.model.RecordPeriod
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDate

class FuelRecordUiStateTest {

    private fun record(
        id: Long = 1,
        date: LocalDate = LocalDate.of(2026, 7, 10),
        volume: Double = 40.0,
        amount: Int = 6800,
        fuelEconomy: Double? = 15.0,
    ) = FuelRecord(
        id = id,
        vehicleId = 1,
        date = date,
        odometer = 50000,
        volume = volume,
        amount = amount,
        fuelEconomy = fuelEconomy,
    )

    @Test
    fun `filteredAverageEconomy returns average of filtered records`() {
        val state = FuelRecordUiState(
            records = listOf(
                record(id = 1, fuelEconomy = 14.0),
                record(id = 2, fuelEconomy = 16.0),
            ),
            selectedPeriod = RecordPeriod.ALL,
        )
        assertEquals(15.0, state.filteredAverageEconomy!!, 0.001)
    }

    @Test
    fun `filteredAverageEconomy is null when no records have economy`() {
        val state = FuelRecordUiState(
            records = listOf(record(fuelEconomy = null)),
            selectedPeriod = RecordPeriod.ALL,
        )
        assertNull(state.filteredAverageEconomy)
    }

    @Test
    fun `filteredAverageEconomy is null when records is empty`() {
        val state = FuelRecordUiState(records = emptyList())
        assertNull(state.filteredAverageEconomy)
    }

    @Test
    fun `filteredTotalAmount sums amounts of filtered records`() {
        val state = FuelRecordUiState(
            records = listOf(
                record(id = 1, amount = 6000),
                record(id = 2, amount = 7000),
            ),
            selectedPeriod = RecordPeriod.ALL,
        )
        assertEquals(13000, state.filteredTotalAmount)
    }

    @Test
    fun `filteredTotalAmount is zero when records is empty`() {
        val state = FuelRecordUiState(records = emptyList())
        assertEquals(0, state.filteredTotalAmount)
    }

    @Test
    fun `filteredTotalVolume sums volumes of filtered records`() {
        val state = FuelRecordUiState(
            records = listOf(
                record(id = 1, volume = 35.5),
                record(id = 2, volume = 42.0),
            ),
            selectedPeriod = RecordPeriod.ALL,
        )
        assertEquals(77.5, state.filteredTotalVolume, 0.001)
    }

    @Test
    fun `filteredTotalVolume is zero when records is empty`() {
        val state = FuelRecordUiState(records = emptyList())
        assertEquals(0.0, state.filteredTotalVolume, 0.001)
    }

    @Test
    fun `filteredRecords respects period filter`() {
        val now = LocalDate.now()
        val recent = record(id = 1, date = now.minusDays(5))
        val old = record(id = 2, date = now.minusMonths(3))
        val state = FuelRecordUiState(
            records = listOf(recent, old),
            selectedPeriod = RecordPeriod.MONTH_1,
        )
        assertEquals(1, state.filteredRecords.size)
        assertEquals(1L, state.filteredRecords.first().id)
    }
}
