package com.segnities007.stylish_myvehicles.presentation.screen.vehicleedit

import com.segnities007.stylish_myvehicles.domain.model.VehicleCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class VehicleEditUiStateTest {

    // ─── makerError ────────────────────────────────────────────────────

    @Test
    fun `メーカーが空の場合makerErrorにメッセージが返る`() {
        // Arrange（準備）
        val state = VehicleEditUiState(maker = "", name = "プリウス")

        // Act & Assert（実行・検証）
        assertEquals("メーカーは必須です", state.makerError)
    }

    @Test
    fun `メーカーが空白のみの場合makerErrorにメッセージが返る`() {
        // Arrange（準備）
        val state = VehicleEditUiState(maker = "   ", name = "プリウス")

        // Act & Assert（実行・検証）
        assertEquals("メーカーは必須です", state.makerError)
    }

    @Test
    fun `メーカーが入力されている場合makerErrorはnull`() {
        // Arrange（準備）
        val state = VehicleEditUiState(maker = "トヨタ", name = "プリウス")

        // Act & Assert（実行・検証）
        assertNull(state.makerError)
    }

    // ─── nameError ─────────────────────────────────────────────────────

    @Test
    fun `車種名が空の場合nameErrorにメッセージが返る`() {
        // Arrange（準備）
        val state = VehicleEditUiState(maker = "トヨタ", name = "")

        // Act & Assert（実行・検証）
        assertEquals("車種名は必須です", state.nameError)
    }

    @Test
    fun `車種名が入力されている場合nameErrorはnull`() {
        // Arrange（準備）
        val state = VehicleEditUiState(maker = "トヨタ", name = "プリウス")

        // Act & Assert（実行・検証）
        assertNull(state.nameError)
    }

    // ─── yearError ─────────────────────────────────────────────────────

    @Test
    fun `年式が空の場合yearErrorはnull`() {
        // Arrange（準備）
        val state = VehicleEditUiState(maker = "トヨタ", name = "プリウス", year = "")

        // Act & Assert（実行・検証）
        assertNull(state.yearError)
    }

    @Test
    fun `年式が数値でない場合yearErrorにメッセージが返る`() {
        // Arrange（準備）
        val state = VehicleEditUiState(maker = "トヨタ", name = "プリウス", year = "abc")

        // Act & Assert（実行・検証）
        assertEquals("数値で入力してください", state.yearError)
    }

    @Test
    fun `年式が1900未満の場合yearErrorに範囲エラーが返る`() {
        // Arrange（準備）
        val state = VehicleEditUiState(maker = "トヨタ", name = "プリウス", year = "1899")

        // Act & Assert（実行・検証）
        assertEquals("1900〜2100の範囲で入力してください", state.yearError)
    }

    @Test
    fun `年式が2100超の場合yearErrorに範囲エラーが返る`() {
        // Arrange（準備）
        val state = VehicleEditUiState(maker = "トヨタ", name = "プリウス", year = "2101")

        // Act & Assert（実行・検証）
        assertEquals("1900〜2100の範囲で入力してください", state.yearError)
    }

    @Test
    fun `年式が有効範囲の場合yearErrorはnull`() {
        // Arrange（準備）
        val state = VehicleEditUiState(maker = "トヨタ", name = "プリウス", year = "2024")

        // Act & Assert（実行・検証）
        assertNull(state.yearError)
    }

    @Test
    fun `年式の境界値1900は有効`() {
        // Arrange（準備）
        val state = VehicleEditUiState(maker = "トヨタ", name = "プリウス", year = "1900")

        // Act & Assert（実行・検証）
        assertNull(state.yearError)
    }

    @Test
    fun `年式の境界値2100は有効`() {
        // Arrange（準備）
        val state = VehicleEditUiState(maker = "トヨタ", name = "プリウス", year = "2100")

        // Act & Assert（実行・検証）
        assertNull(state.yearError)
    }

    // ─── displacementError ─────────────────────────────────────────────

    @Test
    fun `排気量が空の場合displacementErrorはnull`() {
        // Arrange（準備）
        val state = VehicleEditUiState(maker = "トヨタ", name = "プリウス", displacement = "")

        // Act & Assert（実行・検証）
        assertNull(state.displacementError)
    }

    @Test
    fun `排気量が数値でない場合displacementErrorにメッセージが返る`() {
        // Arrange（準備）
        val state = VehicleEditUiState(maker = "トヨタ", name = "プリウス", displacement = "xyz")

        // Act & Assert（実行・検証）
        assertEquals("数値で入力してください", state.displacementError)
    }

    @Test
    fun `排気量が0の場合displacementErrorにメッセージが返る`() {
        // Arrange（準備）
        val state = VehicleEditUiState(maker = "トヨタ", name = "プリウス", displacement = "0")

        // Act & Assert（実行・検証）
        assertEquals("0より大きい値を入力してください", state.displacementError)
    }

    @Test
    fun `排気量が負の値の場合displacementErrorにメッセージが返る`() {
        // Arrange（準備）
        val state = VehicleEditUiState(maker = "トヨタ", name = "プリウス", displacement = "-100")

        // Act & Assert（実行・検証）
        assertEquals("0より大きい値を入力してください", state.displacementError)
    }

    @Test
    fun `排気量が正の値の場合displacementErrorはnull`() {
        // Arrange（準備）
        val state = VehicleEditUiState(maker = "トヨタ", name = "プリウス", displacement = "1800")

        // Act & Assert（実行・検証）
        assertNull(state.displacementError)
    }

    // ─── weightError ───────────────────────────────────────────────────

    @Test
    fun `車両重量が空の場合weightErrorはnull`() {
        // Arrange（準備）
        val state = VehicleEditUiState(maker = "トヨタ", name = "プリウス", weight = "")

        // Act & Assert（実行・検証）
        assertNull(state.weightError)
    }

    @Test
    fun `車両重量が0の場合weightErrorにメッセージが返る`() {
        // Arrange（準備）
        val state = VehicleEditUiState(maker = "トヨタ", name = "プリウス", weight = "0")

        // Act & Assert（実行・検証）
        assertEquals("0より大きい値を入力してください", state.weightError)
    }

    @Test
    fun `車両重量が有効な場合weightErrorはnull`() {
        // Arrange（準備）
        val state = VehicleEditUiState(maker = "トヨタ", name = "プリウス", weight = "1350")

        // Act & Assert（実行・検証）
        assertNull(state.weightError)
    }

    // ─── maxLoadKgError ────────────────────────────────────────────────

    @Test
    fun `最大積載量が空の場合maxLoadKgErrorはnull`() {
        // Arrange（準備）
        val state = VehicleEditUiState(maker = "トヨタ", name = "プリウス", maxLoadKg = "")

        // Act & Assert（実行・検証）
        assertNull(state.maxLoadKgError)
    }

    @Test
    fun `最大積載量が0の場合maxLoadKgErrorにメッセージが返る`() {
        // Arrange（準備）
        val state = VehicleEditUiState(maker = "トヨタ", name = "プリウス", maxLoadKg = "0")

        // Act & Assert（実行・検証）
        assertEquals("0より大きい値を入力してください", state.maxLoadKgError)
    }

    @Test
    fun `最大積載量が有効な場合maxLoadKgErrorはnull`() {
        // Arrange（準備）
        val state = VehicleEditUiState(maker = "トヨタ", name = "プリウス", maxLoadKg = "2000")

        // Act & Assert（実行・検証）
        assertNull(state.maxLoadKgError)
    }

    // ─── insuranceRankError ────────────────────────────────────────────

    @Test
    fun `保険等級が空の場合insuranceRankErrorはnull`() {
        // Arrange（準備）
        val state = VehicleEditUiState(maker = "トヨタ", name = "プリウス", insuranceRank = "")

        // Act & Assert（実行・検証）
        assertNull(state.insuranceRankError)
    }

    @Test
    fun `保険等級が数値でない場合insuranceRankErrorにメッセージが返る`() {
        // Arrange（準備）
        val state = VehicleEditUiState(maker = "トヨタ", name = "プリウス", insuranceRank = "abc")

        // Act & Assert（実行・検証）
        assertEquals("数値で入力してください", state.insuranceRankError)
    }

    @Test
    fun `保険等級が0の場合insuranceRankErrorに範囲エラーが返る`() {
        // Arrange（準備）
        val state = VehicleEditUiState(maker = "トヨタ", name = "プリウス", insuranceRank = "0")

        // Act & Assert（実行・検証）
        assertEquals("1〜20の範囲で入力してください", state.insuranceRankError)
    }

    @Test
    fun `保険等級が21の場合insuranceRankErrorに範囲エラーが返る`() {
        // Arrange（準備）
        val state = VehicleEditUiState(maker = "トヨタ", name = "プリウス", insuranceRank = "21")

        // Act & Assert（実行・検証）
        assertEquals("1〜20の範囲で入力してください", state.insuranceRankError)
    }

    @Test
    fun `保険等級の境界値1は有効`() {
        // Arrange（準備）
        val state = VehicleEditUiState(maker = "トヨタ", name = "プリウス", insuranceRank = "1")

        // Act & Assert（実行・検証）
        assertNull(state.insuranceRankError)
    }

    @Test
    fun `保険等級の境界値20は有効`() {
        // Arrange（準備）
        val state = VehicleEditUiState(maker = "トヨタ", name = "プリウス", insuranceRank = "20")

        // Act & Assert（実行・検証）
        assertNull(state.insuranceRankError)
    }

    // ─── canSave 複合検証 ──────────────────────────────────────────────

    @Test
    fun `全必須項目が有効で保存中でない場合canSaveはtrue`() {
        // Arrange（準備）
        val state = VehicleEditUiState(
            maker = "トヨタ",
            name = "プリウス",
            year = "2024",
            displacement = "1800",
            weight = "1350",
            insuranceRank = "20",
        )

        // Act & Assert（実行・検証）
        assertTrue(state.canSave)
    }

    @Test
    fun `メーカーが空の場合canSaveはfalse`() {
        // Arrange（準備）
        val state = VehicleEditUiState(maker = "", name = "プリウス")

        // Act & Assert（実行・検証）
        assertFalse(state.canSave)
    }

    @Test
    fun `車種名が空の場合canSaveはfalse`() {
        // Arrange（準備）
        val state = VehicleEditUiState(maker = "トヨタ", name = "")

        // Act & Assert（実行・検証）
        assertFalse(state.canSave)
    }

    @Test
    fun `年式が無効な場合canSaveはfalse`() {
        // Arrange（準備）
        val state = VehicleEditUiState(maker = "トヨタ", name = "プリウス", year = "abc")

        // Act & Assert（実行・検証）
        assertFalse(state.canSave)
    }

    @Test
    fun `排気量が無効な場合canSaveはfalse`() {
        // Arrange（準備）
        val state = VehicleEditUiState(maker = "トヨタ", name = "プリウス", displacement = "-1")

        // Act & Assert（実行・検証）
        assertFalse(state.canSave)
    }

    @Test
    fun `保険等級が無効な場合canSaveはfalse`() {
        // Arrange（準備）
        val state = VehicleEditUiState(maker = "トヨタ", name = "プリウス", insuranceRank = "25")

        // Act & Assert（実行・検証）
        assertFalse(state.canSave)
    }

    @Test
    fun `保存中の場合canSaveはfalse`() {
        // Arrange（準備）
        val state = VehicleEditUiState(
            maker = "トヨタ",
            name = "プリウス",
            isSaving = true,
        )

        // Act & Assert（実行・検証）
        assertFalse(state.canSave)
    }

    @Test
    fun `トラックで最大積載量が無効な場合canSaveはfalse`() {
        // Arrange（準備）
        val state = VehicleEditUiState(
            maker = "日野",
            name = "プロフィア",
            category = VehicleCategory.TRUCK,
            maxLoadKg = "0",
        )

        // Act & Assert（実行・検証）
        assertFalse(state.canSave)
    }

    @Test
    fun `トラック以外では最大積載量が無効でもcanSaveに影響しない`() {
        // Arrange（準備）
        val state = VehicleEditUiState(
            maker = "トヨタ",
            name = "プリウス",
            category = VehicleCategory.CAR,
            maxLoadKg = "0",
        )

        // Act & Assert（実行・検証）
        assertTrue(state.canSave)
    }
}
