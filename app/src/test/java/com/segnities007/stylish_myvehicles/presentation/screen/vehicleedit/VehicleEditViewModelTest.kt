package com.segnities007.stylish_myvehicles.presentation.screen.vehicleedit

import app.cash.turbine.test
import com.segnities007.stylish_myvehicles.domain.model.Vehicle
import com.segnities007.stylish_myvehicles.domain.model.VehicleCategory
import com.segnities007.stylish_myvehicles.domain.usecase.vehicle.InsertVehicleUseCase
import com.segnities007.stylish_myvehicles.test.FakeMaintenanceScheduleRepository
import com.segnities007.stylish_myvehicles.test.FakeVehicleRepository
import java.time.LocalDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
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
class VehicleEditViewModelTest {

    private lateinit var vehicleRepo: FakeVehicleRepository
    private lateinit var scheduleRepo: FakeMaintenanceScheduleRepository
    private lateinit var insertUseCase: InsertVehicleUseCase

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        vehicleRepo = FakeVehicleRepository()
        scheduleRepo = FakeMaintenanceScheduleRepository()
        insertUseCase = InsertVehicleUseCase(vehicleRepo, scheduleRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(vehicleId: Long? = null) =
        VehicleEditViewModel(vehicleId, vehicleRepo, insertUseCase)

    @Test
    fun `新規作成モードで初期状態が正しい`() = runTest {
        // Arrange（準備）
        // vehicleId = null で新規作成モード

        // Act（実行）
        val vm = createViewModel(vehicleId = null)

        // Assert（検証）
        val state = vm.uiState.value
        assertEquals(VehicleCategory.CAR, state.category)
        assertEquals("", state.maker)
        assertEquals("", state.name)
        assertFalse(state.isEditing)
        assertFalse(state.isSaving)
    }

    @Test
    fun `編集モードで車両データが読み込まれる`() = runTest {
        // Arrange（準備）
        vehicleRepo.seed(
            Vehicle(
                id = 1, category = VehicleCategory.KEI_CAR,
                maker = "スズキ", name = "ワゴンR", grade = "FX",
                year = 2023, color = "白",
            ),
        )

        // Act（実行）
        val vm = createViewModel(vehicleId = 1)

        // Assert（検証）
        val state = vm.uiState.value
        assertTrue(state.isEditing)
        assertEquals(VehicleCategory.KEI_CAR, state.category)
        assertEquals("スズキ", state.maker)
        assertEquals("ワゴンR", state.name)
        assertEquals("FX", state.grade)
        assertEquals("2023", state.year)
        assertEquals("白", state.color)
    }

    @Test
    fun `メーカーと車種が必須バリデーションされる`() = runTest {
        // Arrange（準備）
        val vm = createViewModel(vehicleId = null)

        // Act & Assert（実行・検証）
        // 初期状態ではメーカー・車種が空なので保存不可
        assertFalse(vm.uiState.value.canSave)
        assertNotNull(vm.uiState.value.makerError)
        assertNotNull(vm.uiState.value.nameError)

        // メーカーのみ入力
        vm.accept(VehicleEditIntent.MakerChanged("トヨタ"))
        assertFalse(vm.uiState.value.canSave)

        // 車種も入力すると保存可能
        vm.accept(VehicleEditIntent.NameChanged("カローラ"))
        assertTrue(vm.uiState.value.canSave)
    }

    @Test
    fun `保存すると車両がリポジトリに追加される`() = runTest {
        // Arrange（準備）
        val vm = createViewModel(vehicleId = null)
        vm.accept(VehicleEditIntent.MakerChanged("トヨタ"))
        vm.accept(VehicleEditIntent.NameChanged("カローラ"))

        // Act（実行）
        vm.effects.test {
            vm.accept(VehicleEditIntent.Save)
            assertEquals(VehicleEditEffect.NavigateBack, awaitItem())
        }

        // Assert（検証）
        val vehicles = vehicleRepo.getAll().first()
        assertEquals(1, vehicles.size)
        assertEquals("トヨタ", vehicles.first().maker)
        assertEquals("カローラ", vehicles.first().name)
        // メンテナンススケジュールの初期データも作成される
        assertEquals(1, scheduleRepo.insertedDefaults.size)
    }

    @Test
    fun `編集モードで保存すると更新される`() = runTest {
        // Arrange（準備）
        vehicleRepo.seed(
            Vehicle(id = 1, maker = "ホンダ", name = "フィット"),
        )
        val vm = createViewModel(vehicleId = 1)
        vm.accept(VehicleEditIntent.MakerChanged("日産"))
        vm.accept(VehicleEditIntent.NameChanged("ノート"))

        // Act（実行）
        vm.effects.test {
            vm.accept(VehicleEditIntent.Save)
            assertEquals(VehicleEditEffect.NavigateBack, awaitItem())
        }

        // Assert（検証）
        val vehicles = vehicleRepo.getAll().first()
        assertEquals(1, vehicles.size)
        assertEquals("日産", vehicles.first().maker)
        assertEquals("ノート", vehicles.first().name)
    }

    @Test
    fun `削除すると車両が削除される`() = runTest {
        // Arrange（準備）
        vehicleRepo.seed(
            Vehicle(id = 1, maker = "トヨタ", name = "カローラ"),
        )
        val vm = createViewModel(vehicleId = 1)
        vm.accept(VehicleEditIntent.RequestDelete)
        assertTrue(vm.uiState.value.showDeleteDialog)

        // Act（実行）
        vm.effects.test {
            vm.accept(VehicleEditIntent.ConfirmDelete)
            assertEquals(VehicleEditEffect.NavigateBack, awaitItem())
        }

        // Assert（検証）
        val vehicles = vehicleRepo.getAll().first()
        assertTrue(vehicles.isEmpty())
    }

    @Test
    fun `車検期限が自動計算される（普通車は初度登録から3年）`() = runTest {
        // Arrange（準備）
        val firstRegDate = LocalDate.of(2026, 4, 1)
        val vm = createViewModel(vehicleId = null)
        vm.accept(VehicleEditIntent.MakerChanged("トヨタ"))
        vm.accept(VehicleEditIntent.NameChanged("カローラ"))
        vm.accept(VehicleEditIntent.CategoryChanged(VehicleCategory.CAR))
        vm.accept(VehicleEditIntent.FirstRegistrationDateChanged(firstRegDate))

        // Act（実行）
        vm.effects.test {
            vm.accept(VehicleEditIntent.Save)
            assertEquals(VehicleEditEffect.NavigateBack, awaitItem())
        }

        // Assert（検証）
        // 普通車は初度登録から3年後が初回車検満了日: 2026-04-01 + 3年 = 2029-04-01
        val vehicle = vehicleRepo.getAll().first().first()
        assertEquals(LocalDate.of(2029, 4, 1), vehicle.inspectionExpiry)
    }

    @Test
    fun `各インテントでUiStateのフィールドが更新される`() = runTest {
        // Arrange（準備）
        val vm = createViewModel(vehicleId = null)

        // Act（実行）
        vm.accept(VehicleEditIntent.GradeChanged("G"))
        vm.accept(VehicleEditIntent.YearChanged("2024"))
        vm.accept(VehicleEditIntent.ModelCodeChanged("ZRE212"))
        vm.accept(VehicleEditIntent.PlateNumberChanged("品川300あ1234"))
        vm.accept(VehicleEditIntent.DisplacementChanged("1800"))
        vm.accept(VehicleEditIntent.WeightChanged("1300"))
        vm.accept(VehicleEditIntent.MaxLoadKgChanged("500"))
        vm.accept(VehicleEditIntent.ColorChanged("ホワイト"))
        vm.accept(VehicleEditIntent.InsuranceCompanyChanged("東京海上"))
        vm.accept(VehicleEditIntent.InsuranceRankChanged("15"))

        // Assert（検証）
        val state = vm.uiState.value
        assertEquals("G", state.grade)
        assertEquals("2024", state.year)
        assertEquals("ZRE212", state.modelCode)
        assertEquals("品川300あ1234", state.plateNumber)
        assertEquals("1800", state.displacement)
        assertEquals("1300", state.weight)
        assertEquals("500", state.maxLoadKg)
        assertEquals("ホワイト", state.color)
        assertEquals("東京海上", state.insuranceCompany)
        assertEquals("15", state.insuranceRank)
    }

    @Test
    fun `日付変更インテントでUiStateが更新される`() = runTest {
        // Arrange（準備）
        val vm = createViewModel(vehicleId = null)
        val date = LocalDate.of(2026, 4, 1)

        // Act（実行）
        vm.accept(VehicleEditIntent.FirstRegistrationDateChanged(date))
        vm.accept(VehicleEditIntent.InspectionExpiryChanged(date.plusYears(3)))
        vm.accept(VehicleEditIntent.JibaiExpiryChanged(date.plusYears(2)))
        vm.accept(VehicleEditIntent.InsuranceExpiryChanged(date.plusYears(1)))

        // Assert（検証）
        val state = vm.uiState.value
        assertEquals(date, state.firstRegistrationDate)
        assertEquals(date.plusYears(3), state.inspectionExpiry)
        assertEquals(date.plusYears(2), state.jibaiExpiry)
        assertEquals(date.plusYears(1), state.insuranceExpiry)
    }

    @Test
    fun `vehicleIdがnullの場合ConfirmDeleteしても何も起きない`() = runTest {
        // Arrange（準備）
        val vm = createViewModel(vehicleId = null)
        vm.accept(VehicleEditIntent.RequestDelete)

        // Act（実行）— vehicleId が null なので削除されない
        vm.accept(VehicleEditIntent.ConfirmDelete)

        // Assert（検証）— エフェクトは発行されない
        val vehicles = vehicleRepo.getAll().first()
        assertTrue(vehicles.isEmpty())
    }

    @Test
    fun `DismissDeleteDialogで削除ダイアログが閉じる`() = runTest {
        // Arrange（準備）
        vehicleRepo.seed(Vehicle(id = 1, maker = "トヨタ", name = "カローラ"))
        val vm = createViewModel(vehicleId = 1)
        vm.accept(VehicleEditIntent.RequestDelete)
        assertTrue(vm.uiState.value.showDeleteDialog)

        // Act（実行）
        vm.accept(VehicleEditIntent.DismissDeleteDialog)

        // Assert（検証）
        assertFalse(vm.uiState.value.showDeleteDialog)
    }

    @Test
    fun `NavigateBackインテントでエフェクトが発行される`() = runTest {
        // Arrange（準備）
        val vm = createViewModel(vehicleId = null)

        // Act（実行）
        vm.effects.test {
            vm.accept(VehicleEditIntent.NavigateBack)
            assertEquals(VehicleEditEffect.NavigateBack, awaitItem())
        }
    }

    @Test
    fun `年式が4桁に制限される`() = runTest {
        // Arrange（準備）
        val vm = createViewModel(vehicleId = null)

        // Act（実行）
        vm.accept(VehicleEditIntent.YearChanged("12345"))

        // Assert（検証）
        assertEquals("1234", vm.uiState.value.year)
    }

    @Test
    fun `保険等級が2桁に制限される`() = runTest {
        // Arrange（準備）
        val vm = createViewModel(vehicleId = null)

        // Act（実行）
        vm.accept(VehicleEditIntent.InsuranceRankChanged("123"))

        // Assert（検証）
        assertEquals("12", vm.uiState.value.insuranceRank)
    }

    @Test
    fun `年式のバリデーションエラーが正しく判定される`() = runTest {
        // Arrange（準備）
        val vm = createViewModel(vehicleId = null)

        // Act & Assert（実行・検証）
        vm.accept(VehicleEditIntent.MakerChanged("トヨタ"))
        vm.accept(VehicleEditIntent.NameChanged("カローラ"))

        // 範囲外の年式
        vm.accept(VehicleEditIntent.YearChanged("1800"))
        assertNotNull(vm.uiState.value.yearError)
        assertFalse(vm.uiState.value.canSave)

        // 有効な年式
        vm.accept(VehicleEditIntent.YearChanged("2020"))
        assertNull(vm.uiState.value.yearError)
        assertTrue(vm.uiState.value.canSave)
    }

    @Test
    fun `排気量のバリデーションエラーが正しく判定される`() = runTest {
        // Arrange（準備）
        val vm = createViewModel(vehicleId = null)
        vm.accept(VehicleEditIntent.MakerChanged("トヨタ"))
        vm.accept(VehicleEditIntent.NameChanged("カローラ"))

        // Act & Assert（実行・検証）
        vm.accept(VehicleEditIntent.DisplacementChanged("0"))
        assertNotNull(vm.uiState.value.displacementError)

        vm.accept(VehicleEditIntent.DisplacementChanged("1800"))
        assertNull(vm.uiState.value.displacementError)
    }

    @Test
    fun `車両重量のバリデーションエラーが正しく判定される`() = runTest {
        // Arrange（準備）
        val vm = createViewModel(vehicleId = null)
        vm.accept(VehicleEditIntent.MakerChanged("トヨタ"))
        vm.accept(VehicleEditIntent.NameChanged("カローラ"))

        // Act & Assert（実行・検証）— 0は正値ではないのでエラー
        vm.accept(VehicleEditIntent.WeightChanged("0"))
        assertNotNull(vm.uiState.value.weightError)

        vm.accept(VehicleEditIntent.WeightChanged("1300"))
        assertNull(vm.uiState.value.weightError)
    }

    @Test
    fun `保険等級のバリデーションエラーが正しく判定される`() = runTest {
        // Arrange（準備）
        val vm = createViewModel(vehicleId = null)
        vm.accept(VehicleEditIntent.MakerChanged("トヨタ"))
        vm.accept(VehicleEditIntent.NameChanged("カローラ"))

        // Act & Assert（実行・検証）
        vm.accept(VehicleEditIntent.InsuranceRankChanged("25"))
        assertNotNull(vm.uiState.value.insuranceRankError)
        assertFalse(vm.uiState.value.canSave)

        vm.accept(VehicleEditIntent.InsuranceRankChanged("15"))
        assertNull(vm.uiState.value.insuranceRankError)
        assertTrue(vm.uiState.value.canSave)
    }

    @Test
    fun `トラックの場合最大積載量のバリデーションが有効になる`() = runTest {
        // Arrange（準備）
        val vm = createViewModel(vehicleId = null)
        vm.accept(VehicleEditIntent.MakerChanged("いすゞ"))
        vm.accept(VehicleEditIntent.NameChanged("エルフ"))
        vm.accept(VehicleEditIntent.CategoryChanged(VehicleCategory.TRUCK))

        // Act & Assert（実行・検証）
        vm.accept(VehicleEditIntent.MaxLoadKgChanged("0"))
        assertNotNull(vm.uiState.value.maxLoadKgError)
        assertFalse(vm.uiState.value.canSave)

        vm.accept(VehicleEditIntent.MaxLoadKgChanged("2000"))
        assertNull(vm.uiState.value.maxLoadKgError)
        assertTrue(vm.uiState.value.canSave)
    }

    @Test
    fun `カテゴリ変更がUiStateに反映される`() = runTest {
        // Arrange（準備）
        val vm = createViewModel(vehicleId = null)

        // Act（実行）
        vm.accept(VehicleEditIntent.CategoryChanged(VehicleCategory.MOTORCYCLE))

        // Assert（検証）
        assertEquals(VehicleCategory.MOTORCYCLE, vm.uiState.value.category)
    }

    @Test
    fun `編集モードで全フィールドが読み込まれる`() = runTest {
        // Arrange（準備）
        vehicleRepo.seed(
            Vehicle(
                id = 1, category = VehicleCategory.CAR,
                maker = "トヨタ", name = "カローラ", grade = "G",
                year = 2020, modelCode = "ZRE212",
                plateNumber = "品川300あ1234",
                displacement = 1800, weight = 1300, maxLoadKg = null,
                color = "ホワイト",
                firstRegistrationDate = LocalDate.of(2020, 4, 1),
                inspectionExpiry = LocalDate.of(2025, 4, 1),
                jibaiExpiry = LocalDate.of(2025, 4, 1),
                insuranceExpiry = LocalDate.of(2026, 4, 1),
                insuranceCompany = "東京海上",
                insuranceRank = 15,
            ),
        )

        // Act（実行）
        val vm = createViewModel(vehicleId = 1)

        // Assert（検証）
        val state = vm.uiState.value
        assertEquals("ZRE212", state.modelCode)
        assertEquals("品川300あ1234", state.plateNumber)
        assertEquals("1800", state.displacement)
        assertEquals("1300", state.weight)
        assertEquals("", state.maxLoadKg)
        assertEquals(LocalDate.of(2020, 4, 1), state.firstRegistrationDate)
        assertEquals(LocalDate.of(2025, 4, 1), state.inspectionExpiry)
        assertEquals("東京海上", state.insuranceCompany)
        assertEquals("15", state.insuranceRank)
    }
}
