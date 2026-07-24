package com.segnities007.stylish_myvehicles.presentation.screen.fuel

import app.cash.turbine.test
import com.segnities007.stylish_myvehicles.domain.model.FuelRecord
import com.segnities007.stylish_myvehicles.domain.usecase.fuel.InsertFuelRecordUseCase
import com.segnities007.stylish_myvehicles.test.FakeCostRecordRepository
import com.segnities007.stylish_myvehicles.test.FakeFuelRecordRepository
import java.time.LocalDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FuelRecordViewModelTest {

    private lateinit var fuelRepo: FakeFuelRecordRepository
    private lateinit var costRepo: FakeCostRecordRepository
    private lateinit var insertUseCase: InsertFuelRecordUseCase

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        fuelRepo = FakeFuelRecordRepository()
        costRepo = FakeCostRecordRepository()
        insertUseCase = InsertFuelRecordUseCase(fuelRepo, costRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(vehicleId: Long = 1L) =
        FuelRecordViewModel(vehicleId, fuelRepo, insertUseCase)

    @Test
    fun `初期状態で給油記録が読み込まれる`() = runTest {
        // Arrange（準備）
        fuelRepo.seed(
            FuelRecord(
                id = 1, vehicleId = 1, date = LocalDate.of(2026, 7, 1),
                odometer = 10000, volume = 30.0, amount = 5000,
            ),
            FuelRecord(
                id = 2, vehicleId = 1, date = LocalDate.of(2026, 7, 10),
                odometer = 10500, volume = 25.0, amount = 4000,
            ),
        )

        // Act（実行）
        val vm = createViewModel()

        // Assert（検証）
        vm.uiState.test {
            val state = awaitItem()
            assertEquals(2, state.records.size)
            assertFalse(state.isLoading)
            assertEquals(10000, state.records[0].odometer)
            assertEquals(10500, state.records[1].odometer)
        }
    }

    @Test
    fun `給油記録を保存するとリポジトリに追加される`() = runTest {
        // Arrange（準備）
        val vm = createViewModel()
        vm.accept(FuelRecordIntent.OpenAddDialog)
        vm.accept(FuelRecordIntent.OdometerChanged("10000"))
        vm.accept(FuelRecordIntent.VolumeChanged("30.0"))
        vm.accept(FuelRecordIntent.AmountChanged("5000"))

        // Act（実行）
        vm.accept(FuelRecordIntent.Save)

        // Assert（検証）
        val state = vm.uiState.value
        assertFalse(state.isDialogOpen)
        assertEquals(1, state.records.size)
        val saved = state.records.first()
        assertEquals(10000, saved.odometer)
        assertEquals(30.0, saved.volume, 0.01)
        assertEquals(5000, saved.amount)
        assertEquals(1L, saved.vehicleId)
    }

    @Test
    fun `満タン給油で燃費が計算される`() = runTest {
        // Arrange（準備）
        fuelRepo.seed(
            FuelRecord(
                id = 1, vehicleId = 1, date = LocalDate.of(2026, 7, 1),
                odometer = 10000, volume = 30.0, amount = 5000, isFullTank = true,
            ),
        )
        val vm = createViewModel()
        vm.accept(FuelRecordIntent.OpenAddDialog)
        vm.accept(FuelRecordIntent.OdometerChanged("10500"))
        vm.accept(FuelRecordIntent.VolumeChanged("50.0"))
        vm.accept(FuelRecordIntent.AmountChanged("8000"))
        vm.accept(FuelRecordIntent.FullTankChanged(true))

        // Act（実行）
        vm.accept(FuelRecordIntent.Save)

        // Assert（検証）
        // (10500 - 10000) / 50.0 = 10.0 km/L
        val newRecord = vm.uiState.value.records.find { it.odometer == 10500 }
        assertEquals(10.0, newRecord?.fuelEconomy ?: 0.0, 0.01)
    }

    @Test
    fun `満タンでない場合燃費がnullになる`() = runTest {
        // Arrange（準備）
        fuelRepo.seed(
            FuelRecord(
                id = 1, vehicleId = 1, date = LocalDate.of(2026, 7, 1),
                odometer = 10000, volume = 30.0, amount = 5000, isFullTank = true,
            ),
        )
        val vm = createViewModel()
        vm.accept(FuelRecordIntent.OpenAddDialog)
        vm.accept(FuelRecordIntent.OdometerChanged("10500"))
        vm.accept(FuelRecordIntent.VolumeChanged("50.0"))
        vm.accept(FuelRecordIntent.AmountChanged("8000"))
        vm.accept(FuelRecordIntent.FullTankChanged(false))

        // Act（実行）
        vm.accept(FuelRecordIntent.Save)

        // Assert（検証）
        val newRecord = vm.uiState.value.records.find { it.odometer == 10500 }
        assertNull(newRecord?.fuelEconomy)
    }

    @Test
    fun `編集モードで保存すると更新される`() = runTest {
        // Arrange（準備）
        fuelRepo.seed(
            FuelRecord(
                id = 1, vehicleId = 1, date = LocalDate.of(2026, 7, 1),
                odometer = 10000, volume = 30.0, amount = 5000,
            ),
        )
        val vm = createViewModel()
        vm.accept(FuelRecordIntent.EditRecord(1))
        assertTrue(vm.uiState.value.isEditing)
        vm.accept(FuelRecordIntent.VolumeChanged("40.0"))
        vm.accept(FuelRecordIntent.AmountChanged("6000"))

        // Act（実行）
        vm.accept(FuelRecordIntent.Save)

        // Assert（検証）
        val updated = vm.uiState.value.records.find { it.id == 1L }
        assertEquals(40.0, updated?.volume ?: 0.0, 0.01)
        assertEquals(6000, updated?.amount)
        assertFalse(vm.uiState.value.isDialogOpen)
    }

    @Test
    fun `削除確認で記録が削除される`() = runTest {
        // Arrange（準備）
        fuelRepo.seed(
            FuelRecord(
                id = 1, vehicleId = 1, date = LocalDate.of(2026, 7, 1),
                odometer = 10000, volume = 30.0, amount = 5000,
            ),
        )
        val vm = createViewModel()
        vm.accept(FuelRecordIntent.RequestDelete(1))
        assertEquals(1L, vm.uiState.value.deletingRecordId)

        // Act（実行）
        vm.accept(FuelRecordIntent.ConfirmDelete)

        // Assert（検証）
        assertTrue(vm.uiState.value.records.isEmpty())
        assertNull(vm.uiState.value.deletingRecordId)
    }

    @Test
    fun `走行距離が前回の値で自動入力される`() = runTest {
        // Arrange（準備）
        fuelRepo.seed(
            FuelRecord(
                id = 1, vehicleId = 1, date = LocalDate.of(2026, 7, 10),
                odometer = 12345, volume = 30.0, amount = 5000,
            ),
        )
        val vm = createViewModel()

        // Act（実行）
        vm.accept(FuelRecordIntent.OpenAddDialog)

        // Assert（検証）
        assertEquals("12345", vm.uiState.value.inputOdometer)
        assertTrue(vm.uiState.value.isDialogOpen)
    }

    @Test
    fun `編集ダイアログを開くと既存レコードの全フィールドが反映される`() = runTest {
        // Arrange（準備）
        fuelRepo.seed(
            FuelRecord(
                id = 1, vehicleId = 1, date = LocalDate.of(2026, 7, 1),
                odometer = 10000, volume = 30.5, amount = 5100,
                isFullTank = false, fuelEconomy = 12.5,
            ),
        )
        val vm = createViewModel()

        // Act（実行）
        vm.accept(FuelRecordIntent.EditRecord(1))

        // Assert（検証）
        val state = vm.uiState.value
        assertTrue(state.isDialogOpen)
        assertTrue(state.isEditing)
        assertEquals(1L, state.editingRecordId)
        assertEquals(LocalDate.of(2026, 7, 1), state.inputDate)
        assertEquals("10000", state.inputOdometer)
        assertEquals("30.5", state.inputVolume)
        assertEquals("5100", state.inputAmount)
        assertFalse(state.inputIsFullTank)
        assertEquals("12.5 km/L", state.calculatedEconomy)
    }

    @Test
    fun `削除ターゲットがない状態でConfirmDeleteしても何も起きない`() = runTest {
        // Arrange（準備）
        fuelRepo.seed(
            FuelRecord(
                id = 1, vehicleId = 1, date = LocalDate.of(2026, 7, 1),
                odometer = 10000, volume = 30.0, amount = 5000,
            ),
        )
        val vm = createViewModel()

        // Act（実行）— deletingRecordId が null のまま ConfirmDelete
        vm.accept(FuelRecordIntent.ConfirmDelete)

        // Assert（検証）— レコードは削除されない
        assertEquals(1, vm.uiState.value.records.size)
    }

    @Test
    fun `DismissDeleteで削除確認がキャンセルされる`() = runTest {
        // Arrange（準備）
        fuelRepo.seed(
            FuelRecord(
                id = 1, vehicleId = 1, date = LocalDate.of(2026, 7, 1),
                odometer = 10000, volume = 30.0, amount = 5000,
            ),
        )
        val vm = createViewModel()
        vm.accept(FuelRecordIntent.RequestDelete(1))
        assertEquals(1L, vm.uiState.value.deletingRecordId)

        // Act（実行）
        vm.accept(FuelRecordIntent.DismissDelete)

        // Assert（検証）
        assertNull(vm.uiState.value.deletingRecordId)
        assertEquals(1, vm.uiState.value.records.size)
    }

    @Test
    fun `単価が自動計算される`() = runTest {
        // Arrange（準備）
        val vm = createViewModel()
        vm.accept(FuelRecordIntent.OpenAddDialog)
        vm.accept(FuelRecordIntent.OdometerChanged("10000"))
        vm.accept(FuelRecordIntent.VolumeChanged("30.0"))
        vm.accept(FuelRecordIntent.AmountChanged("5100"))

        // Act（実行）
        vm.accept(FuelRecordIntent.Save)

        // Assert（検証）— 5100 / 30.0 = 170 円/L
        val saved = vm.uiState.value.records.first()
        assertEquals(170, saved.unitPrice)
    }

    @Test
    fun `NavigateBackインテントでエフェクトが発行される`() = runTest {
        // Arrange（準備）
        val vm = createViewModel()

        // Act（実行）
        vm.accept(FuelRecordIntent.NavigateBack)

        // Assert（検証）
        vm.effects.test {
            assertEquals(FuelRecordEffect.NavigateBack, awaitItem())
        }
    }

    @Test
    fun `ReceiptScannedで入力値が更新される`() = runTest {
        // Arrange（準備）
        val vm = createViewModel()
        vm.accept(FuelRecordIntent.OpenAddDialog)

        // Act（実行）
        vm.accept(FuelRecordIntent.ReceiptScanned(volume = "25.5", amount = "4000", odometer = "11000"))

        // Assert（検証）
        val state = vm.uiState.value
        assertEquals("25.5", state.inputVolume)
        assertEquals("4000", state.inputAmount)
        assertEquals("11000", state.inputOdometer)
    }

    @Test
    fun `ScanningChangedでスキャン状態が切り替わる`() = runTest {
        // Arrange（準備）
        val vm = createViewModel()

        // Act（実行）
        vm.accept(FuelRecordIntent.ScanningChanged(true))

        // Assert（検証）
        assertTrue(vm.uiState.value.isScanning)

        // Act（実行）
        vm.accept(FuelRecordIntent.ScanningChanged(false))

        // Assert（検証）
        assertFalse(vm.uiState.value.isScanning)
    }

    @Test
    fun `CloseDialogでダイアログが閉じ燃費もクリアされる`() = runTest {
        // Arrange（準備）
        fuelRepo.seed(
            FuelRecord(
                id = 1, vehicleId = 1, date = LocalDate.of(2026, 7, 1),
                odometer = 10000, volume = 30.0, amount = 5000, isFullTank = true,
            ),
        )
        val vm = createViewModel()
        vm.accept(FuelRecordIntent.OpenAddDialog)
        vm.accept(FuelRecordIntent.OdometerChanged("10500"))
        vm.accept(FuelRecordIntent.VolumeChanged("50.0"))

        // Act（実行）
        vm.accept(FuelRecordIntent.CloseDialog)

        // Assert（検証）
        assertFalse(vm.uiState.value.isDialogOpen)
        assertNull(vm.uiState.value.editingRecordId)
        assertNull(vm.uiState.value.calculatedEconomy)
    }

    @Test
    fun `満タン切り替え後に再計算すると燃費がnullになる`() = runTest {
        // Arrange（準備）
        fuelRepo.seed(
            FuelRecord(
                id = 1, vehicleId = 1, date = LocalDate.of(2026, 7, 1),
                odometer = 10000, volume = 30.0, amount = 5000, isFullTank = true,
            ),
        )
        val vm = createViewModel()
        vm.accept(FuelRecordIntent.OpenAddDialog)
        vm.accept(FuelRecordIntent.OdometerChanged("10500"))
        vm.accept(FuelRecordIntent.VolumeChanged("50.0"))
        // 満タンで燃費が計算される
        assertNotNull(vm.uiState.value.calculatedEconomy)

        // Act（実行）— 満タンを解除し、再計算をトリガー
        vm.accept(FuelRecordIntent.FullTankChanged(false))
        vm.accept(FuelRecordIntent.VolumeChanged("50.0"))

        // Assert（検証）
        assertNull(vm.uiState.value.calculatedEconomy)
    }

    @Test
    fun `SelectPeriodで選択期間が変更される`() = runTest {
        // Arrange（準備）
        val vm = createViewModel()

        // Act（実行）
        vm.accept(FuelRecordIntent.SelectPeriod(com.segnities007.stylish_myvehicles.domain.model.RecordPeriod.MONTH_1))

        // Assert（検証）
        assertEquals(com.segnities007.stylish_myvehicles.domain.model.RecordPeriod.MONTH_1, vm.uiState.value.selectedPeriod)
    }

    @Test
    fun `日付変更が反映される`() = runTest {
        // Arrange（準備）
        val vm = createViewModel()
        vm.accept(FuelRecordIntent.OpenAddDialog)
        val newDate = LocalDate.of(2026, 8, 15)

        // Act（実行）
        vm.accept(FuelRecordIntent.DateChanged(newDate))

        // Assert（検証）
        assertEquals(newDate, vm.uiState.value.inputDate)
    }

    @Test
    fun `canSaveは必須フィールドが空の場合falseになる`() = runTest {
        // Arrange（準備）
        val vm = createViewModel()
        vm.accept(FuelRecordIntent.OpenAddDialog)

        // Act & Assert（実行・検証）— すべて空
        assertFalse(vm.uiState.value.canSave)

        // 一部入力
        vm.accept(FuelRecordIntent.OdometerChanged("10000"))
        assertFalse(vm.uiState.value.canSave)

        // すべて入力
        vm.accept(FuelRecordIntent.VolumeChanged("30.0"))
        vm.accept(FuelRecordIntent.AmountChanged("5000"))
        assertTrue(vm.uiState.value.canSave)
    }
}
