package com.segnities007.stylish_myvehicles.presentation.screen.cost

import app.cash.turbine.test
import com.segnities007.stylish_myvehicles.domain.model.CostCategory
import com.segnities007.stylish_myvehicles.domain.model.CostRecord
import com.segnities007.stylish_myvehicles.test.FakeCostRecordRepository
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
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CostListViewModelTest {

    private lateinit var costRepo: FakeCostRecordRepository

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        costRepo = FakeCostRecordRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(vehicleId: Long = 1L) =
        CostListViewModel(vehicleId, costRepo)

    @Test
    fun `初期状態で費用記録が読み込まれる`() = runTest {
        // Arrange（準備）
        costRepo.seed(
            CostRecord(
                id = 1, vehicleId = 1, date = LocalDate.of(2026, 7, 1),
                category = CostCategory.FUEL, title = "給油", amount = 5000,
            ),
            CostRecord(
                id = 2, vehicleId = 1, date = LocalDate.of(2026, 7, 5),
                category = CostCategory.MAINTENANCE, title = "オイル交換", amount = 3000,
            ),
        )

        // Act（実行）
        val vm = createViewModel()

        // Assert（検証）
        vm.uiState.test {
            val state = awaitItem()
            assertEquals(2, state.records.size)
            assertFalse(state.isLoading)
        }
    }

    @Test
    fun `費用を保存するとリポジトリに追加される`() = runTest {
        // Arrange（準備）
        val vm = createViewModel()
        vm.accept(CostListIntent.OpenAddDialog)
        vm.accept(CostListIntent.InputCategoryChanged(CostCategory.PARKING))
        vm.accept(CostListIntent.InputTitleChanged("駐車場代"))
        vm.accept(CostListIntent.InputAmountChanged("8000"))

        // Act（実行）
        vm.accept(CostListIntent.Save)

        // Assert（検証）
        val state = vm.uiState.value
        assertFalse(state.isDialogOpen)
        assertEquals(1, state.records.size)
        val saved = state.records.first()
        assertEquals("駐車場代", saved.title)
        assertEquals(8000, saved.amount)
        assertEquals(CostCategory.PARKING, saved.category)
    }

    @Test
    fun `編集モードで保存すると更新される`() = runTest {
        // Arrange（準備）
        costRepo.seed(
            CostRecord(
                id = 1, vehicleId = 1, date = LocalDate.of(2026, 7, 1),
                category = CostCategory.FUEL, title = "給油", amount = 5000,
            ),
        )
        val vm = createViewModel()
        vm.accept(CostListIntent.EditRecord(1))
        assertTrue(vm.uiState.value.isEditing)
        vm.accept(CostListIntent.InputTitleChanged("ハイオク給油"))
        vm.accept(CostListIntent.InputAmountChanged("6000"))

        // Act（実行）
        vm.accept(CostListIntent.Save)

        // Assert（検証）
        val updated = vm.uiState.value.records.find { it.id == 1L }
        assertEquals("ハイオク給油", updated?.title)
        assertEquals(6000, updated?.amount)
        assertFalse(vm.uiState.value.isDialogOpen)
    }

    @Test
    fun `削除確認で記録が削除される`() = runTest {
        // Arrange（準備）
        costRepo.seed(
            CostRecord(
                id = 1, vehicleId = 1, date = LocalDate.of(2026, 7, 1),
                category = CostCategory.FUEL, title = "給油", amount = 5000,
            ),
        )
        val vm = createViewModel()
        vm.accept(CostListIntent.RequestDelete(1))
        assertEquals(1L, vm.uiState.value.deletingRecordId)

        // Act（実行）
        vm.accept(CostListIntent.ConfirmDelete)

        // Assert（検証）
        assertTrue(vm.uiState.value.records.isEmpty())
        assertNull(vm.uiState.value.deletingRecordId)
    }

    @Test
    fun `合計金額が正しく計算される`() = runTest {
        // Arrange（準備）
        costRepo.seed(
            CostRecord(
                id = 1, vehicleId = 1, date = LocalDate.of(2026, 7, 1),
                category = CostCategory.FUEL, title = "給油", amount = 5000,
            ),
            CostRecord(
                id = 2, vehicleId = 1, date = LocalDate.of(2026, 7, 5),
                category = CostCategory.MAINTENANCE, title = "オイル交換", amount = 3000,
            ),
            CostRecord(
                id = 3, vehicleId = 1, date = LocalDate.of(2026, 7, 10),
                category = CostCategory.PARKING, title = "駐車場", amount = 2000,
            ),
        )

        // Act（実行）
        val vm = createViewModel()

        // Assert（検証）
        assertEquals(10000, vm.uiState.value.totalAmount)
    }

    @Test
    fun `編集ダイアログを開くと既存レコードの値が入力欄に反映される`() = runTest {
        // Arrange（準備）
        costRepo.seed(
            CostRecord(
                id = 1, vehicleId = 1, date = LocalDate.of(2026, 6, 15),
                category = CostCategory.TAX, title = "自動車税", amount = 39500,
            ),
        )
        val vm = createViewModel()

        // Act（実行）
        vm.accept(CostListIntent.EditRecord(1))

        // Assert（検証）
        val state = vm.uiState.value
        assertTrue(state.isDialogOpen)
        assertTrue(state.isEditing)
        assertEquals(1L, state.editingRecordId)
        assertEquals(CostCategory.TAX, state.inputCategory)
        assertEquals(LocalDate.of(2026, 6, 15), state.inputDate)
        assertEquals("自動車税", state.inputTitle)
        assertEquals("39500", state.inputAmount)
    }

    @Test
    fun `削除ターゲットがない状態でConfirmDeleteしても何も起きない`() = runTest {
        // Arrange（準備）
        costRepo.seed(
            CostRecord(
                id = 1, vehicleId = 1, date = LocalDate.of(2026, 7, 1),
                category = CostCategory.FUEL, title = "給油", amount = 5000,
            ),
        )
        val vm = createViewModel()

        // Act（実行）— deletingRecordId が null のまま ConfirmDelete
        vm.accept(CostListIntent.ConfirmDelete)

        // Assert（検証）— レコードは削除されない
        assertEquals(1, vm.uiState.value.records.size)
    }

    @Test
    fun `DismissDeleteで削除確認がキャンセルされる`() = runTest {
        // Arrange（準備）
        costRepo.seed(
            CostRecord(
                id = 1, vehicleId = 1, date = LocalDate.of(2026, 7, 1),
                category = CostCategory.FUEL, title = "給油", amount = 5000,
            ),
        )
        val vm = createViewModel()
        vm.accept(CostListIntent.RequestDelete(1))
        assertEquals(1L, vm.uiState.value.deletingRecordId)

        // Act（実行）
        vm.accept(CostListIntent.DismissDelete)

        // Assert（検証）
        assertNull(vm.uiState.value.deletingRecordId)
        assertEquals(1, vm.uiState.value.records.size)
    }

    @Test
    fun `カテゴリ変更でタイトルが自動入力される`() = runTest {
        // Arrange（準備）
        val vm = createViewModel()
        vm.accept(CostListIntent.OpenAddDialog)

        // Act（実行）
        vm.accept(CostListIntent.InputCategoryChanged(CostCategory.TAX))

        // Assert（検証）
        assertEquals(CostCategory.TAX, vm.uiState.value.inputCategory)
        // タイトルが空の場合カテゴリのラベルが自動入力される
        assertEquals("税金", vm.uiState.value.inputTitle)
    }

    @Test
    fun `日付変更が反映される`() = runTest {
        // Arrange（準備）
        val vm = createViewModel()
        vm.accept(CostListIntent.OpenAddDialog)
        val newDate = LocalDate.of(2026, 8, 15)

        // Act（実行）
        vm.accept(CostListIntent.InputDateChanged(newDate))

        // Assert（検証）
        assertEquals(newDate, vm.uiState.value.inputDate)
    }

    @Test
    fun `SelectCategoryでフィルタリングされる`() = runTest {
        // Arrange（準備）
        costRepo.seed(
            CostRecord(
                id = 1, vehicleId = 1, date = LocalDate.of(2026, 7, 1),
                category = CostCategory.FUEL, title = "給油", amount = 5000,
            ),
            CostRecord(
                id = 2, vehicleId = 1, date = LocalDate.of(2026, 7, 5),
                category = CostCategory.MAINTENANCE, title = "オイル交換", amount = 3000,
            ),
        )
        val vm = createViewModel()

        // Act（実行）
        vm.accept(CostListIntent.SelectCategory(CostCategory.FUEL))

        // Assert（検証）
        val state = vm.uiState.value
        assertEquals(1, state.filteredRecords.size)
        assertEquals("給油", state.filteredRecords[0].title)
        assertEquals(5000, state.totalAmount)
    }

    @Test
    fun `NavigateBackインテントでエフェクトが発行される`() = runTest {
        // Arrange（準備）
        val vm = createViewModel()

        // Act（実行）
        vm.accept(CostListIntent.NavigateBack)

        // Assert（検証）
        vm.effects.test {
            assertEquals(CostListEffect.NavigateBack, awaitItem())
        }
    }

    @Test
    fun `CloseDialogでダイアログが閉じカテゴリフィルタもリセットされる`() = runTest {
        // Arrange（準備）
        val vm = createViewModel()
        vm.accept(CostListIntent.OpenAddDialog)
        assertTrue(vm.uiState.value.isDialogOpen)

        // Act（実行）
        vm.accept(CostListIntent.CloseDialog)

        // Assert（検証）
        assertFalse(vm.uiState.value.isDialogOpen)
        assertNull(vm.uiState.value.editingRecordId)
        assertNull(vm.uiState.value.selectedCategory)
    }

    @Test
    fun `タイトルが空の場合保存できない`() = runTest {
        // Arrange（準備）
        val vm = createViewModel()
        vm.accept(CostListIntent.OpenAddDialog)
        vm.accept(CostListIntent.InputAmountChanged("5000"))
        // タイトル未入力

        // Act（実行）
        vm.accept(CostListIntent.Save)

        // Assert（検証）— ダイアログはまだ開いている
        assertTrue(vm.uiState.value.isDialogOpen)
    }

    @Test
    fun `金額が未入力の場合保存できない`() = runTest {
        // Arrange（準備）
        val vm = createViewModel()
        vm.accept(CostListIntent.OpenAddDialog)
        vm.accept(CostListIntent.InputTitleChanged("テスト"))
        // 金額未入力

        // Act（実行）
        vm.accept(CostListIntent.Save)

        // Assert（検証）
        assertTrue(vm.uiState.value.isDialogOpen)
    }

    @Test
    fun `金額入力は整数のみ正規化される`() = runTest {
        // Arrange（準備）
        val vm = createViewModel()
        vm.accept(CostListIntent.OpenAddDialog)

        // Act（実行）
        vm.accept(CostListIntent.InputAmountChanged("5000abc"))

        // Assert（検証）
        assertEquals("5000", vm.uiState.value.inputAmount)
    }

    @Test
    fun `当月と前月の合計が正しく計算される`() = runTest {
        // Arrange（準備）
        val now = LocalDate.now()
        val prevMonth = now.minusMonths(1)
        costRepo.seed(
            CostRecord(
                id = 1, vehicleId = 1, date = now.withDayOfMonth(1),
                category = CostCategory.FUEL, title = "今月の給油", amount = 5000,
            ),
            CostRecord(
                id = 2, vehicleId = 1, date = prevMonth.withDayOfMonth(1),
                category = CostCategory.FUEL, title = "先月の給油", amount = 4000,
            ),
        )

        // Act（実行）
        val vm = createViewModel()

        // Assert（検証）
        assertEquals(5000, vm.uiState.value.monthlyTotal)
        assertEquals(4000, vm.uiState.value.previousMonthTotal)
    }
}
