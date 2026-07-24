package com.segnities007.stylish_myvehicles.presentation.screen.trip

import com.segnities007.stylish_myvehicles.domain.model.TripPurpose
import com.segnities007.stylish_myvehicles.domain.model.TripRecord
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDateTime

class TripRecordUiStateTest {

    private fun tripRecord(
        id: Long = 1,
        isRecording: Boolean = false,
    ) = TripRecord(
        id = id,
        vehicleId = 1,
        title = "テストドライブ",
        purpose = TripPurpose.DRIVE,
        startedAt = LocalDateTime.of(2026, 7, 10, 8, 0),
        endedAt = if (isRecording) null else LocalDateTime.of(2026, 7, 10, 10, 0),
    )

    // ─── activeRecord ──────────────────────────────────────────────────

    @Test
    fun `activeRecordは記録中のレコードを返す`() {
        // Arrange（準備）
        val state = TripRecordUiState(
            records = listOf(
                tripRecord(id = 1, isRecording = false),
                tripRecord(id = 2, isRecording = true),
            ),
        )

        // Act & Assert（実行・検証）
        assertEquals(2L, state.activeRecord?.id)
    }

    @Test
    fun `記録中のレコードがなければactiveRecordはnull`() {
        // Arrange（準備）
        val state = TripRecordUiState(
            records = listOf(tripRecord(id = 1, isRecording = false)),
        )

        // Act & Assert（実行・検証）
        assertNull(state.activeRecord)
    }

    @Test
    fun `レコードが空の場合activeRecordはnull`() {
        // Arrange（準備）
        val state = TripRecordUiState(records = emptyList())

        // Act & Assert（実行・検証）
        assertNull(state.activeRecord)
    }

    // ─── editingRecord ─────────────────────────────────────────────────

    @Test
    fun `editingRecordはeditingRecordIdに一致するレコードを返す`() {
        // Arrange（準備）
        val state = TripRecordUiState(
            records = listOf(tripRecord(id = 1), tripRecord(id = 2)),
            editingRecordId = 2L,
        )

        // Act & Assert（実行・検証）
        assertEquals(2L, state.editingRecord?.id)
    }

    @Test
    fun `editingRecordIdがnullの場合editingRecordはnull`() {
        // Arrange（準備）
        val state = TripRecordUiState(
            records = listOf(tripRecord(id = 1)),
            editingRecordId = null,
        )

        // Act & Assert（実行・検証）
        assertNull(state.editingRecord)
    }

    @Test
    fun `editingRecordIdに一致するレコードがなければnull`() {
        // Arrange（準備）
        val state = TripRecordUiState(
            records = listOf(tripRecord(id = 1)),
            editingRecordId = 99L,
        )

        // Act & Assert（実行・検証）
        assertNull(state.editingRecord)
    }

    // ─── canSave ───────────────────────────────────────────────────────

    @Test
    fun `有効な入力の場合canSaveはtrue`() {
        // Arrange（準備）
        val state = TripRecordUiState(
            inputStartTime = "08:00",
            inputEndTime = "10:30",
            inputDistanceKm = "25.5",
        )

        // Act & Assert（実行・検証）
        assertTrue(state.canSave)
    }

    @Test
    fun `開始時刻が無効な場合canSaveはfalse`() {
        // Arrange（準備）
        val state = TripRecordUiState(
            inputStartTime = "invalid",
            inputEndTime = "10:30",
            inputDistanceKm = "25.5",
        )

        // Act & Assert（実行・検証）
        assertFalse(state.canSave)
    }

    @Test
    fun `終了時刻が無効な場合canSaveはfalse`() {
        // Arrange（準備）
        val state = TripRecordUiState(
            inputStartTime = "08:00",
            inputEndTime = "",
            inputDistanceKm = "25.5",
        )

        // Act & Assert（実行・検証）
        assertFalse(state.canSave)
    }

    @Test
    fun `距離が数値でない場合canSaveはfalse`() {
        // Arrange（準備）
        val state = TripRecordUiState(
            inputStartTime = "08:00",
            inputEndTime = "10:30",
            inputDistanceKm = "abc",
        )

        // Act & Assert（実行・検証）
        assertFalse(state.canSave)
    }

    @Test
    fun `距離が空の場合canSaveはfalse`() {
        // Arrange（準備）
        val state = TripRecordUiState(
            inputStartTime = "08:00",
            inputEndTime = "10:30",
            inputDistanceKm = "",
        )

        // Act & Assert（実行・検証）
        assertFalse(state.canSave)
    }
}
