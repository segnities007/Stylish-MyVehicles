package com.segnities007.stylish_myvehicles.presentation.screen.vehicledetail

import com.segnities007.stylish_myvehicles.domain.model.Vehicle
import com.segnities007.stylish_myvehicles.domain.model.VehicleCategory
import com.segnities007.stylish_myvehicles.presentation.components.organisms.VehicleField
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class VehicleDetailUiStateTest {

    @Test
    fun `デフォルト状態ではisLoadingがtrueでvehicleがnull`() {
        // Arrange（準備）
        // Act（実行）
        val state = VehicleDetailUiState()

        // Assert（検証）
        assertTrue(state.isLoading)
        assertNull(state.vehicle)
        assertFalse(state.taxPaidThisYear)
        assertNull(state.editingField)
        assertEquals("", state.fieldInputText)
        assertNull(state.fieldInputDate)
        assertEquals(VehicleCategory.CAR, state.fieldInputCategory)
    }

    @Test
    fun `vehicleが設定されている場合プロパティにアクセスできる`() {
        // Arrange（準備）
        val vehicle = Vehicle(
            id = 1,
            maker = "トヨタ",
            name = "プリウス",
            grade = "S",
            year = 2024,
            category = VehicleCategory.CAR,
        )

        // Act（実行）
        val state = VehicleDetailUiState(vehicle = vehicle, isLoading = false)

        // Assert（検証）
        assertFalse(state.isLoading)
        assertEquals("トヨタ", state.vehicle?.maker)
        assertEquals("プリウス", state.vehicle?.name)
        assertEquals("S", state.vehicle?.grade)
        assertEquals(2024, state.vehicle?.year)
    }

    @Test
    fun `taxPaidThisYearがtrueの場合自動車税納付済みを示す`() {
        // Arrange（準備）
        // Act（実行）
        val state = VehicleDetailUiState(taxPaidThisYear = true)

        // Assert（検証）
        assertTrue(state.taxPaidThisYear)
    }

    @Test
    fun `editingFieldが設定されている場合ダイアログ編集中を示す`() {
        // Arrange（準備）
        val state = VehicleDetailUiState(
            editingField = VehicleField.MAKER,
            fieldInputText = "トヨタ",
        )

        // Act & Assert（実行・検証）
        assertEquals(VehicleField.MAKER, state.editingField)
        assertEquals("トヨタ", state.fieldInputText)
    }

    @Test
    fun `日付フィールド編集中はfieldInputDateに値が設定される`() {
        // Arrange（準備）
        val date = LocalDate.of(2027, 3, 31)
        val state = VehicleDetailUiState(
            editingField = VehicleField.INSPECTION_EXPIRY,
            fieldInputDate = date,
        )

        // Act & Assert（実行・検証）
        assertEquals(VehicleField.INSPECTION_EXPIRY, state.editingField)
        assertEquals(date, state.fieldInputDate)
    }

    @Test
    fun `カテゴリフィールド編集中はfieldInputCategoryに値が設定される`() {
        // Arrange（準備）
        val state = VehicleDetailUiState(
            editingField = VehicleField.CATEGORY,
            fieldInputCategory = VehicleCategory.TRUCK,
        )

        // Act & Assert（実行・検証）
        assertEquals(VehicleCategory.TRUCK, state.fieldInputCategory)
    }
}
