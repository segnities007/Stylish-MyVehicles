package com.segnities007.stylish_myvehicles.presentation.screen.maintenance

import app.cash.turbine.test
import com.segnities007.stylish_myvehicles.domain.model.MaintenanceCategory
import com.segnities007.stylish_myvehicles.domain.model.MaintenanceRecord
import com.segnities007.stylish_myvehicles.domain.model.Vehicle
import com.segnities007.stylish_myvehicles.domain.model.VehicleCategory
import com.segnities007.stylish_myvehicles.domain.usecase.maintenance.InsertMaintenanceRecordUseCase
import com.segnities007.stylish_myvehicles.test.FakeCostRecordRepository
import com.segnities007.stylish_myvehicles.test.FakeMaintenanceRecordRepository
import com.segnities007.stylish_myvehicles.test.FakeMaintenanceScheduleRepository
import com.segnities007.stylish_myvehicles.test.FakeVehicleRepository
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
class MaintenanceRecordViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var maintenanceRecordRepository: FakeMaintenanceRecordRepository
    private lateinit var costRecordRepository: FakeCostRecordRepository
    private lateinit var scheduleRepository: FakeMaintenanceScheduleRepository
    private lateinit var vehicleRepository: FakeVehicleRepository
    private lateinit var insertUseCase: InsertMaintenanceRecordUseCase

    private val vehicleId = 1L
    private val testVehicle = Vehicle(
        id = vehicleId,
        maker = "トヨタ",
        name = "カローラ",
        category = VehicleCategory.CAR,
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        maintenanceRecordRepository = FakeMaintenanceRecordRepository()
        costRecordRepository = FakeCostRecordRepository()
        scheduleRepository = FakeMaintenanceScheduleRepository()
        vehicleRepository = FakeVehicleRepository()
        insertUseCase = InsertMaintenanceRecordUseCase(
            maintenanceRecordRepository,
            costRecordRepository,
            scheduleRepository,
        )
        vehicleRepository.seed(testVehicle)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): MaintenanceRecordViewModel =
        MaintenanceRecordViewModel(
            vehicleId = vehicleId,
            maintenanceRecordRepository = maintenanceRecordRepository,
            insertMaintenanceRecordUseCase = insertUseCase,
            vehicleRepository = vehicleRepository,
            maintenanceScheduleRepository = scheduleRepository,
        )

    @Test
    fun `初期状態で整備記録が読み込まれる`() = runTest {
        // Arrange（準備）
        val record1 = MaintenanceRecord(
            id = 1L,
            vehicleId = vehicleId,
            date = LocalDate.of(2026, 7, 1),
            category = MaintenanceCategory.OIL,
            title = "オイル交換",
            cost = 3000,
        )
        val record2 = MaintenanceRecord(
            id = 2L,
            vehicleId = vehicleId,
            date = LocalDate.of(2026, 7, 15),
            category = MaintenanceCategory.TIRE,
            title = "タイヤ交換",
            cost = 40000,
        )
        maintenanceRecordRepository.seed(record1, record2)

        // Act（実行）
        val viewModel = createViewModel()

        // Assert（検証）
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(2, state.records.size)
        assertEquals("オイル交換", state.records[0].title)
        assertEquals("タイヤ交換", state.records[1].title)
    }

    @Test
    fun `整備記録を保存するとリポジトリに追加される`() = runTest {
        // Arrange（準備）
        val viewModel = createViewModel()
        viewModel.accept(MaintenanceRecordIntent.OpenAddDialog)
        viewModel.accept(MaintenanceRecordIntent.TitleChanged("ブレーキパッド交換"))
        viewModel.accept(MaintenanceRecordIntent.CostChanged("15000"))
        viewModel.accept(MaintenanceRecordIntent.CategoryChanged(MaintenanceCategory.BRAKE))

        // Act（実行）
        viewModel.accept(MaintenanceRecordIntent.Save)

        // Assert（検証）
        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isDialogOpen)
        }
    }

    @Test
    fun `削除確認で記録が削除される`() = runTest {
        // Arrange（準備）
        val record = MaintenanceRecord(
            id = 1L,
            vehicleId = vehicleId,
            date = LocalDate.of(2026, 7, 1),
            category = MaintenanceCategory.OIL,
            title = "オイル交換",
            cost = 3000,
        )
        maintenanceRecordRepository.seed(record)
        val viewModel = createViewModel()

        // Act（実行）
        viewModel.accept(MaintenanceRecordIntent.RequestDelete(recordId = 1L))
        viewModel.accept(MaintenanceRecordIntent.ConfirmDelete)

        // Assert（検証）
        val state = viewModel.uiState.value
        assertEquals(0, state.records.size)
        assertEquals(null, state.deletingRecordId)
    }

    @Test
    fun `カテゴリに応じたデフォルトタイトルが設定される`() = runTest {
        // Arrange（準備）
        val viewModel = createViewModel()
        viewModel.accept(MaintenanceRecordIntent.OpenAddDialog)

        // Act（実行）
        viewModel.accept(MaintenanceRecordIntent.CategoryChanged(MaintenanceCategory.OIL))

        // Assert（検証）
        val state = viewModel.uiState.value
        assertEquals(MaintenanceCategory.OIL, state.inputCategory)
        assertEquals("エンジンオイル", state.inputTitle)
    }

    @Test
    fun `編集ダイアログを開くと既存レコードの値が入力欄に反映される`() = runTest {
        // Arrange（準備）
        val record = MaintenanceRecord(
            id = 1L,
            vehicleId = vehicleId,
            date = LocalDate.of(2026, 6, 15),
            odometer = 25000,
            category = MaintenanceCategory.BRAKE,
            title = "ブレーキパッド交換",
            cost = 15000,
            shopName = "オートバックス",
        )
        maintenanceRecordRepository.seed(record)
        val viewModel = createViewModel()

        // Act（実行）
        viewModel.accept(MaintenanceRecordIntent.EditRecord(recordId = 1L))

        // Assert（検証）
        val state = viewModel.uiState.value
        assertTrue(state.isDialogOpen)
        assertTrue(state.isEditing)
        assertEquals(1L, state.editingRecordId)
        assertEquals(LocalDate.of(2026, 6, 15), state.inputDate)
        assertEquals("25000", state.inputOdometer)
        assertEquals(MaintenanceCategory.BRAKE, state.inputCategory)
        assertEquals("ブレーキパッド交換", state.inputTitle)
        assertEquals("15000", state.inputCost)
        assertEquals("オートバックス", state.inputShopName)
    }

    @Test
    fun `編集モードで保存するとレコードが更新される`() = runTest {
        // Arrange（準備）
        val record = MaintenanceRecord(
            id = 1L,
            vehicleId = vehicleId,
            date = LocalDate.of(2026, 6, 15),
            category = MaintenanceCategory.OIL,
            title = "オイル交換",
            cost = 3000,
        )
        maintenanceRecordRepository.seed(record)
        val viewModel = createViewModel()
        viewModel.accept(MaintenanceRecordIntent.EditRecord(recordId = 1L))
        viewModel.accept(MaintenanceRecordIntent.TitleChanged("オイル＆フィルター交換"))
        viewModel.accept(MaintenanceRecordIntent.CostChanged("5000"))

        // Act（実行）
        viewModel.accept(MaintenanceRecordIntent.Save)

        // Assert（検証）
        val state = viewModel.uiState.value
        assertFalse(state.isDialogOpen)
        val updated = state.records.find { it.id == 1L }
        assertEquals("オイル＆フィルター交換", updated?.title)
        assertEquals(5000, updated?.cost)
    }

    @Test
    fun `削除ターゲットがない状態でConfirmDeleteしても何も起きない`() = runTest {
        // Arrange（準備）
        val record = MaintenanceRecord(
            id = 1L,
            vehicleId = vehicleId,
            date = LocalDate.of(2026, 7, 1),
            category = MaintenanceCategory.OIL,
            title = "オイル交換",
            cost = 3000,
        )
        maintenanceRecordRepository.seed(record)
        val viewModel = createViewModel()

        // Act（実行）— deletingRecordId が null のまま ConfirmDelete
        viewModel.accept(MaintenanceRecordIntent.ConfirmDelete)

        // Assert（検証）— レコードは削除されない
        assertEquals(1, viewModel.uiState.value.records.size)
    }

    @Test
    fun `DismissDeleteで削除確認がキャンセルされる`() = runTest {
        // Arrange（準備）
        val record = MaintenanceRecord(
            id = 1L,
            vehicleId = vehicleId,
            date = LocalDate.of(2026, 7, 1),
            category = MaintenanceCategory.OIL,
            title = "オイル交換",
            cost = 3000,
        )
        maintenanceRecordRepository.seed(record)
        val viewModel = createViewModel()
        viewModel.accept(MaintenanceRecordIntent.RequestDelete(recordId = 1L))
        assertEquals(1L, viewModel.uiState.value.deletingRecordId)

        // Act（実行）
        viewModel.accept(MaintenanceRecordIntent.DismissDelete)

        // Assert（検証）
        assertNull(viewModel.uiState.value.deletingRecordId)
        assertEquals(1, viewModel.uiState.value.records.size)
    }

    @Test
    fun `カスタムタイトルはカテゴリ変更で上書きされない`() = runTest {
        // Arrange（準備）
        val viewModel = createViewModel()
        viewModel.accept(MaintenanceRecordIntent.OpenAddDialog)
        viewModel.accept(MaintenanceRecordIntent.TitleChanged("カスタム整備内容"))

        // Act（実行）
        viewModel.accept(MaintenanceRecordIntent.CategoryChanged(MaintenanceCategory.TIRE))

        // Assert（検証）— カスタムタイトルは維持される
        assertEquals("カスタム整備内容", viewModel.uiState.value.inputTitle)
        assertEquals(MaintenanceCategory.TIRE, viewModel.uiState.value.inputCategory)
    }

    @Test
    fun `カテゴリの自動入力タイトルは別カテゴリに変更すると切り替わる`() = runTest {
        // Arrange（準備）
        val viewModel = createViewModel()
        viewModel.accept(MaintenanceRecordIntent.OpenAddDialog)
        viewModel.accept(MaintenanceRecordIntent.CategoryChanged(MaintenanceCategory.OIL))
        assertEquals("エンジンオイル", viewModel.uiState.value.inputTitle)

        // Act（実行）
        viewModel.accept(MaintenanceRecordIntent.CategoryChanged(MaintenanceCategory.BRAKE))

        // Assert（検証）— 自動タイトルが新しいカテゴリに切り替わる
        assertEquals("ブレーキ", viewModel.uiState.value.inputTitle)
    }

    @Test
    fun `スケジュール期限アイテムが計算される`() = runTest {
        // Arrange（準備）
        val now = LocalDate.now()
        scheduleRepository.seed(
            com.segnities007.stylish_myvehicles.domain.model.MaintenanceSchedule(
                id = 1L,
                vehicleId = vehicleId,
                category = MaintenanceCategory.OIL,
                intervalMonths = 6,
                lastDoneDate = now.minusMonths(5),
            ),
        )

        // Act（実行）
        val viewModel = createViewModel()

        // Assert（検証）
        val dueItems = viewModel.uiState.value.scheduleDueItems
        assertEquals(1, dueItems.size)
        assertEquals("エンジンオイル", dueItems[0].categoryLabel)
        assertTrue(dueItems[0].daysRemaining in 25..35)
    }

    @Test
    fun `NavigateBackインテントでエフェクトが発行される`() = runTest {
        // Arrange（準備）
        val viewModel = createViewModel()

        // Act（実行）
        viewModel.accept(MaintenanceRecordIntent.NavigateBack)

        // Assert（検証）
        viewModel.effects.test {
            assertEquals(MaintenanceRecordEffect.NavigateBack, awaitItem())
        }
    }

    @Test
    fun `SelectPeriodで選択期間が変更される`() = runTest {
        // Arrange（準備）
        val viewModel = createViewModel()

        // Act（実行）
        viewModel.accept(MaintenanceRecordIntent.SelectPeriod(com.segnities007.stylish_myvehicles.domain.model.RecordPeriod.MONTH_3))

        // Assert（検証）
        assertEquals(com.segnities007.stylish_myvehicles.domain.model.RecordPeriod.MONTH_3, viewModel.uiState.value.selectedPeriod)
    }

    @Test
    fun `CloseDialogでダイアログが閉じる`() = runTest {
        // Arrange（準備）
        val viewModel = createViewModel()
        viewModel.accept(MaintenanceRecordIntent.OpenAddDialog)
        assertTrue(viewModel.uiState.value.isDialogOpen)

        // Act（実行）
        viewModel.accept(MaintenanceRecordIntent.CloseDialog)

        // Assert（検証）
        assertFalse(viewModel.uiState.value.isDialogOpen)
        assertNull(viewModel.uiState.value.editingRecordId)
    }

    @Test
    fun `タイトルが空の場合保存できない`() = runTest {
        // Arrange（準備）
        val viewModel = createViewModel()
        viewModel.accept(MaintenanceRecordIntent.OpenAddDialog)
        // タイトル未入力

        // Act（実行）
        viewModel.accept(MaintenanceRecordIntent.Save)

        // Assert（検証）— ダイアログはまだ開いている
        assertTrue(viewModel.uiState.value.isDialogOpen)
    }

    @Test
    fun `費用が0のとき編集ダイアログでは空文字で表示される`() = runTest {
        // Arrange（準備）
        val record = MaintenanceRecord(
            id = 1L,
            vehicleId = vehicleId,
            date = LocalDate.of(2026, 7, 1),
            category = MaintenanceCategory.OIL,
            title = "オイル交換",
            cost = 0,
        )
        maintenanceRecordRepository.seed(record)
        val viewModel = createViewModel()

        // Act（実行）
        viewModel.accept(MaintenanceRecordIntent.EditRecord(recordId = 1L))

        // Assert（検証）
        assertEquals("", viewModel.uiState.value.inputCost)
    }

    @Test
    fun `オドメーター入力は整数のみ正規化される`() = runTest {
        // Arrange（準備）
        val viewModel = createViewModel()
        viewModel.accept(MaintenanceRecordIntent.OpenAddDialog)

        // Act（実行）
        viewModel.accept(MaintenanceRecordIntent.OdometerChanged("123abc"))

        // Assert（検証）
        assertEquals("123", viewModel.uiState.value.inputOdometer)
    }

    @Test
    fun `ショップ名が保存される`() = runTest {
        // Arrange（準備）
        val viewModel = createViewModel()
        viewModel.accept(MaintenanceRecordIntent.OpenAddDialog)
        viewModel.accept(MaintenanceRecordIntent.TitleChanged("オイル交換"))
        viewModel.accept(MaintenanceRecordIntent.ShopNameChanged("イエローハット"))

        // Act（実行）
        viewModel.accept(MaintenanceRecordIntent.Save)

        // Assert（検証）
        val saved = viewModel.uiState.value.records.first()
        assertEquals("イエローハット", saved.shopName)
    }
}
