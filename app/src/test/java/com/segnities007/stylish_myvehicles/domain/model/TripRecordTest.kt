package com.segnities007.stylish_myvehicles.domain.model

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDateTime

class TripRecordTest {
    private val startedAt = LocalDateTime.of(2026, 7, 23, 10, 0)

    @Test
    fun `trip without end time is recording`() {
        val trip = TripRecord(vehicleId = 1, startedAt = startedAt)

        assertTrue(trip.isRecording)
    }

    @Test
    fun `trip with end time is completed`() {
        val trip = TripRecord(
            vehicleId = 1,
            startedAt = startedAt,
            endedAt = startedAt.plusHours(1),
        )

        assertFalse(trip.isRecording)
    }
}
