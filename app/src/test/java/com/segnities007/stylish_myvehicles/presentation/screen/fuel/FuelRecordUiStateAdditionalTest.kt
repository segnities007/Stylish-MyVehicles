package com.segnities007.stylish_myvehicles.presentation.screen.fuel

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FuelRecordUiStateAdditionalTest {

    // ─── canSave ───────────────────────────────────────────────────────

    @Test
    fun `全入力が有効な場合canSaveはtrue`() {
        // Arrange（準備）
        val state = FuelRecordUiState(
            inputOdometer = "10000",
            inputVolume = "30.5",
            inputAmount = "5000",
        )

        // Act & Assert（実行・検証）
        assertTrue(state.canSave)
    }

    @Test
    fun `走行距離が空の場合canSaveはfalse`() {
        // Arrange（準備）
        val state = FuelRecordUiState(
            inputOdometer = "",
            inputVolume = "30.5",
            inputAmount = "5000",
        )

        // Act & Assert（実行・検証）
        assertFalse(state.canSave)
    }

    @Test
    fun `給油量が空の場合canSaveはfalse`() {
        // Arrange（準備）
        val state = FuelRecordUiState(
            inputOdometer = "10000",
            inputVolume = "",
            inputAmount = "5000",
        )

        // Act & Assert（実行・検証）
        assertFalse(state.canSave)
    }

    @Test
    fun `金額が空の場合canSaveはfalse`() {
        // Arrange（準備）
        val state = FuelRecordUiState(
            inputOdometer = "10000",
            inputVolume = "30.5",
            inputAmount = "",
        )

        // Act & Assert（実行・検証）
        assertFalse(state.canSave)
    }

    @Test
    fun `走行距離が数値でない場合canSaveはfalse`() {
        // Arrange（準備）
        val state = FuelRecordUiState(
            inputOdometer = "abc",
            inputVolume = "30.5",
            inputAmount = "5000",
        )

        // Act & Assert（実行・検証）
        assertFalse(state.canSave)
    }

    @Test
    fun `給油量が数値でない場合canSaveはfalse`() {
        // Arrange（準備）
        val state = FuelRecordUiState(
            inputOdometer = "10000",
            inputVolume = "xyz",
            inputAmount = "5000",
        )

        // Act & Assert（実行・検証）
        assertFalse(state.canSave)
    }

    @Test
    fun `金額が数値でない場合canSaveはfalse`() {
        // Arrange（準備）
        val state = FuelRecordUiState(
            inputOdometer = "10000",
            inputVolume = "30.5",
            inputAmount = "def",
        )

        // Act & Assert（実行・検証）
        assertFalse(state.canSave)
    }

    // ─── odometerError ─────────────────────────────────────────────────

    @Test
    fun `走行距離が空の場合odometerErrorに必須メッセージが返る`() {
        // Arrange（準備）
        val state = FuelRecordUiState(inputOdometer = "")

        // Act & Assert（実行・検証）
        assertEquals("走行距離は必須です", state.odometerError)
    }

    @Test
    fun `走行距離が数値でない場合odometerErrorに数値エラーが返る`() {
        // Arrange（準備）
        val state = FuelRecordUiState(inputOdometer = "12.5")

        // Act & Assert（実行・検証）
        assertEquals("数値で入力してください", state.odometerError)
    }

    @Test
    fun `走行距離が有効な整数の場合odometerErrorはnull`() {
        // Arrange（準備）
        val state = FuelRecordUiState(inputOdometer = "10000")

        // Act & Assert（実行・検証）
        assertNull(state.odometerError)
    }

    // ─── volumeError ───────────────────────────────────────────────────

    @Test
    fun `給油量が空の場合volumeErrorに必須メッセージが返る`() {
        // Arrange（準備）
        val state = FuelRecordUiState(inputVolume = "")

        // Act & Assert（実行・検証）
        assertEquals("給油量は必須です", state.volumeError)
    }

    @Test
    fun `給油量が数値でない場合volumeErrorに数値エラーが返る`() {
        // Arrange（準備）
        val state = FuelRecordUiState(inputVolume = "abc")

        // Act & Assert（実行・検証）
        assertEquals("数値で入力してください", state.volumeError)
    }

    @Test
    fun `給油量が有効な数値の場合volumeErrorはnull`() {
        // Arrange（準備）
        val state = FuelRecordUiState(inputVolume = "30.5")

        // Act & Assert（実行・検証）
        assertNull(state.volumeError)
    }

    @Test
    fun `給油量が整数の場合もvolumeErrorはnull`() {
        // Arrange（準備）
        val state = FuelRecordUiState(inputVolume = "30")

        // Act & Assert（実行・検証）
        assertNull(state.volumeError)
    }

    // ─── amountError ───────────────────────────────────────────────────

    @Test
    fun `金額が空の場合amountErrorに必須メッセージが返る`() {
        // Arrange（準備）
        val state = FuelRecordUiState(inputAmount = "")

        // Act & Assert（実行・検証）
        assertEquals("金額は必須です", state.amountError)
    }

    @Test
    fun `金額が数値でない場合amountErrorに数値エラーが返る`() {
        // Arrange（準備）
        val state = FuelRecordUiState(inputAmount = "50.5")

        // Act & Assert（実行・検証）
        assertEquals("数値で入力してください", state.amountError)
    }

    @Test
    fun `金額が有効な整数の場合amountErrorはnull`() {
        // Arrange（準備）
        val state = FuelRecordUiState(inputAmount = "5000")

        // Act & Assert（実行・検証）
        assertNull(state.amountError)
    }

    // ─── isEditing ─────────────────────────────────────────────────────

    @Test
    fun `editingRecordIdがnullの場合isEditingはfalse`() {
        // Arrange（準備）
        val state = FuelRecordUiState(editingRecordId = null)

        // Act & Assert（実行・検証）
        assertFalse(state.isEditing)
    }

    @Test
    fun `editingRecordIdが設定されている場合isEditingはtrue`() {
        // Arrange（準備）
        val state = FuelRecordUiState(editingRecordId = 42L)

        // Act & Assert（実行・検証）
        assertTrue(state.isEditing)
    }
}
