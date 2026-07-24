package com.segnities007.stylish_myvehicles.presentation.screen.vehicledetail

import app.cash.turbine.test
import com.segnities007.stylish_myvehicles.domain.model.CostCategory
import com.segnities007.stylish_myvehicles.domain.model.CostRecord
import com.segnities007.stylish_myvehicles.domain.model.FuelRecord
import com.segnities007.stylish_myvehicles.domain.model.MaintenanceCategory
import com.segnities007.stylish_myvehicles.domain.model.MaintenanceRecord
import com.segnities007.stylish_myvehicles.domain.model.Vehicle
import com.segnities007.stylish_myvehicles.domain.model.VehicleCategory
import com.segnities007.stylish_myvehicles.presentation.components.organisms.VehicleField
import com.segnities007.stylish_myvehicles.test.FakeCostRecordRepository
import com.segnities007.stylish_myvehicles.test.FakeFuelRecordRepository
import com.segnities007.stylish_myvehicles.test.FakeMaintenanceRecordRepository
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
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class VehicleDetailViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var vehicleRepository: FakeVehicleRepository
    private lateinit var fuelRecordRepository: FakeFuelRecordRepository
    private lateinit var maintenanceRecordRepository: FakeMaintenanceRecordRepository
    private lateinit var costRecordRepository: FakeCostRecordRepository

    private val vehicleId = 1L
    private val testVehicle = Vehicle(
        id = vehicleId,
        maker = "トヨタ",
        name = "カローラ",
        grade = "G",
        category = VehicleCategory.CAR,
        year = 2020,
        color = "ホワイト",
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        vehicleRepository = FakeVehicleRepository()
        fuelRecordRepository = FakeFuelRecordRepository()
        maintenanceRecordRepository = FakeMaintenanceRecordRepository()
        costRecordRepository = FakeCostRecordRepository()
        vehicleRepository.seed(testVehicle)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): VehicleDetailViewModel =
        VehicleDetailViewModel(
            vehicleId = vehicleId,
            vehicleRepository = vehicleRepository,
            fuelRecordRepository = fuelRecordRepository,
            maintenanceRecordRepository = maintenanceRecordRepository,
            costRecordRepository = costRecordRepository,
        )

    @Test
    fun `初期状態で車両詳細が読み込まれる`() = runTest {
        // Arrange（準備）— setUp で車両をシード済み

        // Act（実行）
        val viewModel = createViewModel()

        // Assert（検証）
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.vehicle)
        assertEquals("トヨタ", state.vehicle!!.maker)
        assertEquals("カローラ", state.vehicle!!.name)
        assertEquals("G", state.vehicle!!.grade)
    }

    @Test
    fun `フィールド編集ダイアログが開く`() = runTest {
        // Arrange（準備）
        val viewModel = createViewModel()

        // Act（実行）
        viewModel.accept(VehicleDetailIntent.OpenFieldEditor(VehicleField.MAKER))

        // Assert（検証）
        val state = viewModel.uiState.value
        assertEquals(VehicleField.MAKER, state.editingField)
        assertEquals("トヨタ", state.fieldInputText)
    }

    @Test
    fun `テキストフィールドを保存すると車両が更新される`() = runTest {
        // Arrange（準備）
        val viewModel = createViewModel()
        viewModel.accept(VehicleDetailIntent.OpenFieldEditor(VehicleField.MAKER))
        viewModel.accept(VehicleDetailIntent.FieldTextChanged("ホンダ"))

        // Act（実行）
        viewModel.accept(VehicleDetailIntent.SaveField)

        // Assert（検証）
        val state = viewModel.uiState.value
        assertNull(state.editingField)
        assertEquals("ホンダ", state.vehicle!!.maker)
    }

    @Test
    fun `日付フィールドを保存すると車両が更新される`() = runTest {
        // Arrange（準備）
        val viewModel = createViewModel()
        val newDate = LocalDate.of(2027, 3, 31)
        viewModel.accept(VehicleDetailIntent.OpenFieldEditor(VehicleField.INSPECTION_EXPIRY))
        viewModel.accept(VehicleDetailIntent.FieldDateChanged(newDate))

        // Act（実行）
        viewModel.accept(VehicleDetailIntent.SaveField)

        // Assert（検証）
        val state = viewModel.uiState.value
        assertNull(state.editingField)
        assertEquals(newDate, state.vehicle!!.inspectionExpiry)
    }

    @Test
    fun `カテゴリフィールドを保存すると車両が更新される`() = runTest {
        // Arrange（準備）
        val viewModel = createViewModel()
        viewModel.accept(VehicleDetailIntent.OpenFieldEditor(VehicleField.CATEGORY))
        viewModel.accept(VehicleDetailIntent.FieldCategoryChanged(VehicleCategory.KEI_CAR))

        // Act（実行）
        viewModel.accept(VehicleDetailIntent.SaveField)

        // Assert（検証）
        val state = viewModel.uiState.value
        assertNull(state.editingField)
        assertEquals(VehicleCategory.KEI_CAR, state.vehicle!!.category)
    }

    @Test
    fun `自動車税の納付済みフラグが正しく判定される`() = runTest {
        // Arrange（準備）
        val currentYear = LocalDate.now().year
        costRecordRepository.seed(
            CostRecord(
                id = 1L,
                vehicleId = vehicleId,
                date = LocalDate.of(currentYear, 5, 1),
                category = CostCategory.TAX,
                title = "自動車税",
                amount = 39500,
            ),
        )

        // Act（実行）
        val viewModel = createViewModel()

        // Assert（検証）
        assertTrue(viewModel.uiState.value.taxPaidThisYear)
    }

    @Test
    fun `自動車税が未納の場合フラグがfalseになる`() = runTest {
        // Arrange（準備）— 税金レコードなし

        // Act（実行）
        val viewModel = createViewModel()

        // Assert（検証）
        assertFalse(viewModel.uiState.value.taxPaidThisYear)
    }

    @Test
    fun `NavigateBackインテントでNavigateBackエフェクトが発行される`() = runTest {
        // Arrange（準備）
        val viewModel = createViewModel()

        // Act（実行）
        viewModel.accept(VehicleDetailIntent.NavigateBack)

        // Assert（検証）
        viewModel.effects.test {
            assertEquals(VehicleDetailEffect.NavigateBack, awaitItem())
        }
    }

    @Test
    fun `EditVehicleインテントでNavigateToEditエフェクトが発行される`() = runTest {
        // Arrange（準備）
        val viewModel = createViewModel()

        // Act（実行）
        viewModel.accept(VehicleDetailIntent.EditVehicle)

        // Assert（検証）
        viewModel.effects.test {
            assertEquals(VehicleDetailEffect.NavigateToEdit(vehicleId), awaitItem())
        }
    }

    @Test
    fun `OpenCostListインテントでNavigateToCostエフェクトが発行される`() = runTest {
        // Arrange（準備）
        val viewModel = createViewModel()

        // Act（実行）
        viewModel.accept(VehicleDetailIntent.OpenCostList)

        // Assert（検証）
        viewModel.effects.test {
            assertEquals(VehicleDetailEffect.NavigateToCost(vehicleId), awaitItem())
        }
    }

    @Test
    fun `初度登録日のフィールド編集ダイアログが開く`() = runTest {
        // Arrange（準備）
        val date = LocalDate.of(2020, 4, 1)
        vehicleRepository.seed(testVehicle.copy(firstRegistrationDate = date))
        val viewModel = createViewModel()

        // Act（実行）
        viewModel.accept(VehicleDetailIntent.OpenFieldEditor(VehicleField.FIRST_REGISTRATION_DATE))

        // Assert（検証）
        val state = viewModel.uiState.value
        assertEquals(VehicleField.FIRST_REGISTRATION_DATE, state.editingField)
        assertEquals(date, state.fieldInputDate)
    }

    @Test
    fun `自賠責満了日のフィールド編集ダイアログが開く`() = runTest {
        // Arrange（準備）
        val date = LocalDate.of(2027, 3, 31)
        vehicleRepository.seed(testVehicle.copy(jibaiExpiry = date))
        val viewModel = createViewModel()

        // Act（実行）
        viewModel.accept(VehicleDetailIntent.OpenFieldEditor(VehicleField.JIBAI_EXPIRY))

        // Assert（検証）
        val state = viewModel.uiState.value
        assertEquals(VehicleField.JIBAI_EXPIRY, state.editingField)
        assertEquals(date, state.fieldInputDate)
    }

    @Test
    fun `任意保険満了日のフィールド編集ダイアログが開く`() = runTest {
        // Arrange（準備）
        val date = LocalDate.of(2027, 6, 30)
        vehicleRepository.seed(testVehicle.copy(insuranceExpiry = date))
        val viewModel = createViewModel()

        // Act（実行）
        viewModel.accept(VehicleDetailIntent.OpenFieldEditor(VehicleField.INSURANCE_EXPIRY))

        // Assert（検証）
        val state = viewModel.uiState.value
        assertEquals(VehicleField.INSURANCE_EXPIRY, state.editingField)
        assertEquals(date, state.fieldInputDate)
    }

    @Test
    fun `カテゴリのフィールド編集ダイアログが開く`() = runTest {
        // Arrange（準備）
        val viewModel = createViewModel()

        // Act（実行）
        viewModel.accept(VehicleDetailIntent.OpenFieldEditor(VehicleField.CATEGORY))

        // Assert（検証）
        val state = viewModel.uiState.value
        assertEquals(VehicleField.CATEGORY, state.editingField)
        assertEquals(VehicleCategory.CAR, state.fieldInputCategory)
    }

    @Test
    fun `年式のフィールド編集ダイアログが開く`() = runTest {
        // Arrange（準備）
        val viewModel = createViewModel()

        // Act（実行）
        viewModel.accept(VehicleDetailIntent.OpenFieldEditor(VehicleField.YEAR))

        // Assert（検証）
        val state = viewModel.uiState.value
        assertEquals(VehicleField.YEAR, state.editingField)
        assertEquals("2020", state.fieldInputText)
    }

    @Test
    fun `排気量のフィールド編集ダイアログが開く`() = runTest {
        // Arrange（準備）
        vehicleRepository.seed(testVehicle.copy(displacement = 1800))
        val viewModel = createViewModel()

        // Act（実行）
        viewModel.accept(VehicleDetailIntent.OpenFieldEditor(VehicleField.DISPLACEMENT))

        // Assert（検証）
        assertEquals("1800", viewModel.uiState.value.fieldInputText)
    }

    @Test
    fun `車両重量のフィールド編集ダイアログが開く`() = runTest {
        // Arrange（準備）
        vehicleRepository.seed(testVehicle.copy(weight = 1300))
        val viewModel = createViewModel()

        // Act（実行）
        viewModel.accept(VehicleDetailIntent.OpenFieldEditor(VehicleField.WEIGHT))

        // Assert（検証）
        assertEquals("1300", viewModel.uiState.value.fieldInputText)
    }

    @Test
    fun `最大積載量のフィールド編集ダイアログが開く`() = runTest {
        // Arrange（準備）
        vehicleRepository.seed(testVehicle.copy(maxLoadKg = 500))
        val viewModel = createViewModel()

        // Act（実行）
        viewModel.accept(VehicleDetailIntent.OpenFieldEditor(VehicleField.MAX_LOAD))

        // Assert（検証）
        assertEquals("500", viewModel.uiState.value.fieldInputText)
    }

    @Test
    fun `保険等級のフィールド編集ダイアログが開く`() = runTest {
        // Arrange（準備）
        vehicleRepository.seed(testVehicle.copy(insuranceRank = 15))
        val viewModel = createViewModel()

        // Act（実行）
        viewModel.accept(VehicleDetailIntent.OpenFieldEditor(VehicleField.INSURANCE_RANK))

        // Assert（検証）
        assertEquals("15", viewModel.uiState.value.fieldInputText)
    }

    @Test
    fun `nullの整数フィールドは空文字で開く`() = runTest {
        // Arrange（準備）
        vehicleRepository.seed(testVehicle.copy(year = null, displacement = null))
        val viewModel = createViewModel()

        // Act（実行）
        viewModel.accept(VehicleDetailIntent.OpenFieldEditor(VehicleField.YEAR))

        // Assert（検証）
        assertEquals("", viewModel.uiState.value.fieldInputText)
    }

    @Test
    fun `CloseFieldEditorで編集状態がクリアされる`() = runTest {
        // Arrange（準備）
        val viewModel = createViewModel()
        viewModel.accept(VehicleDetailIntent.OpenFieldEditor(VehicleField.MAKER))
        assertNotNull(viewModel.uiState.value.editingField)

        // Act（実行）
        viewModel.accept(VehicleDetailIntent.CloseFieldEditor)

        // Assert（検証）
        assertNull(viewModel.uiState.value.editingField)
    }

    @Test
    fun `車両名を保存すると更新される`() = runTest {
        // Arrange（準備）
        val viewModel = createViewModel()
        viewModel.accept(VehicleDetailIntent.OpenFieldEditor(VehicleField.NAME))
        viewModel.accept(VehicleDetailIntent.FieldTextChanged("プリウス"))

        // Act（実行）
        viewModel.accept(VehicleDetailIntent.SaveField)

        // Assert（検証）
        assertEquals("プリウス", viewModel.uiState.value.vehicle!!.name)
        assertNull(viewModel.uiState.value.editingField)
    }

    @Test
    fun `グレードを保存すると更新される`() = runTest {
        // Arrange（準備）
        val viewModel = createViewModel()
        viewModel.accept(VehicleDetailIntent.OpenFieldEditor(VehicleField.GRADE))
        viewModel.accept(VehicleDetailIntent.FieldTextChanged("S"))

        // Act（実行）
        viewModel.accept(VehicleDetailIntent.SaveField)

        // Assert（検証）
        assertEquals("S", viewModel.uiState.value.vehicle!!.grade)
    }

    @Test
    fun `年式を保存すると整数で更新される`() = runTest {
        // Arrange（準備）
        val viewModel = createViewModel()
        viewModel.accept(VehicleDetailIntent.OpenFieldEditor(VehicleField.YEAR))
        viewModel.accept(VehicleDetailIntent.FieldTextChanged("2025"))

        // Act（実行）
        viewModel.accept(VehicleDetailIntent.SaveField)

        // Assert（検証）
        assertEquals(2025, viewModel.uiState.value.vehicle!!.year)
    }

    @Test
    fun `排気量を保存すると整数で更新される`() = runTest {
        // Arrange（準備）
        val viewModel = createViewModel()
        viewModel.accept(VehicleDetailIntent.OpenFieldEditor(VehicleField.DISPLACEMENT))
        viewModel.accept(VehicleDetailIntent.FieldTextChanged("2000"))

        // Act（実行）
        viewModel.accept(VehicleDetailIntent.SaveField)

        // Assert（検証）
        assertEquals(2000, viewModel.uiState.value.vehicle!!.displacement)
    }

    @Test
    fun `車両重量を保存すると整数で更新される`() = runTest {
        // Arrange（準備）
        val viewModel = createViewModel()
        viewModel.accept(VehicleDetailIntent.OpenFieldEditor(VehicleField.WEIGHT))
        viewModel.accept(VehicleDetailIntent.FieldTextChanged("1500"))

        // Act（実行）
        viewModel.accept(VehicleDetailIntent.SaveField)

        // Assert（検証）
        assertEquals(1500, viewModel.uiState.value.vehicle!!.weight)
    }

    @Test
    fun `最大積載量を保存すると整数で更新される`() = runTest {
        // Arrange（準備）
        val viewModel = createViewModel()
        viewModel.accept(VehicleDetailIntent.OpenFieldEditor(VehicleField.MAX_LOAD))
        viewModel.accept(VehicleDetailIntent.FieldTextChanged("1000"))

        // Act（実行）
        viewModel.accept(VehicleDetailIntent.SaveField)

        // Assert（検証）
        assertEquals(1000, viewModel.uiState.value.vehicle!!.maxLoadKg)
    }

    @Test
    fun `保険等級を保存すると整数で更新される`() = runTest {
        // Arrange（準備）
        val viewModel = createViewModel()
        viewModel.accept(VehicleDetailIntent.OpenFieldEditor(VehicleField.INSURANCE_RANK))
        viewModel.accept(VehicleDetailIntent.FieldTextChanged("20"))

        // Act（実行）
        viewModel.accept(VehicleDetailIntent.SaveField)

        // Assert（検証）
        assertEquals(20, viewModel.uiState.value.vehicle!!.insuranceRank)
    }

    @Test
    fun `初度登録日を保存すると日付で更新される`() = runTest {
        // Arrange（準備）
        val viewModel = createViewModel()
        val newDate = LocalDate.of(2020, 4, 1)
        viewModel.accept(VehicleDetailIntent.OpenFieldEditor(VehicleField.FIRST_REGISTRATION_DATE))
        viewModel.accept(VehicleDetailIntent.FieldDateChanged(newDate))

        // Act（実行）
        viewModel.accept(VehicleDetailIntent.SaveField)

        // Assert（検証）
        assertEquals(newDate, viewModel.uiState.value.vehicle!!.firstRegistrationDate)
    }

    @Test
    fun `自賠責満了日を保存すると日付で更新される`() = runTest {
        // Arrange（準備）
        val viewModel = createViewModel()
        val newDate = LocalDate.of(2028, 3, 31)
        viewModel.accept(VehicleDetailIntent.OpenFieldEditor(VehicleField.JIBAI_EXPIRY))
        viewModel.accept(VehicleDetailIntent.FieldDateChanged(newDate))

        // Act（実行）
        viewModel.accept(VehicleDetailIntent.SaveField)

        // Assert（検証）
        assertEquals(newDate, viewModel.uiState.value.vehicle!!.jibaiExpiry)
    }

    @Test
    fun `任意保険満了日を保存すると日付で更新される`() = runTest {
        // Arrange（準備）
        val viewModel = createViewModel()
        val newDate = LocalDate.of(2028, 6, 30)
        viewModel.accept(VehicleDetailIntent.OpenFieldEditor(VehicleField.INSURANCE_EXPIRY))
        viewModel.accept(VehicleDetailIntent.FieldDateChanged(newDate))

        // Act（実行）
        viewModel.accept(VehicleDetailIntent.SaveField)

        // Assert（検証）
        assertEquals(newDate, viewModel.uiState.value.vehicle!!.insuranceExpiry)
    }

    @Test
    fun `その他のテキストフィールドを保存すると更新される`() = runTest {
        // Arrange（準備）
        val viewModel = createViewModel()
        viewModel.accept(VehicleDetailIntent.OpenFieldEditor(VehicleField.MODEL_CODE))
        viewModel.accept(VehicleDetailIntent.FieldTextChanged("ZRE212"))

        // Act（実行）
        viewModel.accept(VehicleDetailIntent.SaveField)

        // Assert（検証）
        assertEquals("ZRE212", viewModel.uiState.value.vehicle!!.modelCode)
    }

    @Test
    fun `ナンバーを保存すると更新される`() = runTest {
        // Arrange（準備）
        val viewModel = createViewModel()
        viewModel.accept(VehicleDetailIntent.OpenFieldEditor(VehicleField.PLATE_NUMBER))
        viewModel.accept(VehicleDetailIntent.FieldTextChanged("品川300あ1234"))

        // Act（実行）
        viewModel.accept(VehicleDetailIntent.SaveField)

        // Assert（検証）
        assertEquals("品川300あ1234", viewModel.uiState.value.vehicle!!.plateNumber)
    }

    @Test
    fun `車台番号を保存すると更新される`() = runTest {
        // Arrange（準備）
        val viewModel = createViewModel()
        viewModel.accept(VehicleDetailIntent.OpenFieldEditor(VehicleField.VIN))
        viewModel.accept(VehicleDetailIntent.FieldTextChanged("ABC123456"))

        // Act（実行）
        viewModel.accept(VehicleDetailIntent.SaveField)

        // Assert（検証）
        assertEquals("ABC123456", viewModel.uiState.value.vehicle!!.vin)
    }

    @Test
    fun `カラーを保存すると更新される`() = runTest {
        // Arrange（準備）
        val viewModel = createViewModel()
        viewModel.accept(VehicleDetailIntent.OpenFieldEditor(VehicleField.COLOR))
        viewModel.accept(VehicleDetailIntent.FieldTextChanged("ブラック"))

        // Act（実行）
        viewModel.accept(VehicleDetailIntent.SaveField)

        // Assert（検証）
        assertEquals("ブラック", viewModel.uiState.value.vehicle!!.color)
    }

    @Test
    fun `保険会社を保存すると更新される`() = runTest {
        // Arrange（準備）
        val viewModel = createViewModel()
        viewModel.accept(VehicleDetailIntent.OpenFieldEditor(VehicleField.INSURANCE_COMPANY))
        viewModel.accept(VehicleDetailIntent.FieldTextChanged("東京海上"))

        // Act（実行）
        viewModel.accept(VehicleDetailIntent.SaveField)

        // Assert（検証）
        assertEquals("東京海上", viewModel.uiState.value.vehicle!!.insuranceCompany)
    }

    @Test
    fun `メモを保存すると更新される`() = runTest {
        // Arrange（準備）
        val viewModel = createViewModel()
        viewModel.accept(VehicleDetailIntent.OpenFieldEditor(VehicleField.MEMO))
        viewModel.accept(VehicleDetailIntent.FieldTextChanged("テストメモ"))

        // Act（実行）
        viewModel.accept(VehicleDetailIntent.SaveField)

        // Assert（検証）
        assertEquals("テストメモ", viewModel.uiState.value.vehicle!!.memo)
    }

    @Test
    fun `給油CSVエクスポートでSaveDocumentエフェクトが発行される`() = runTest {
        // Arrange（準備）
        fuelRecordRepository.seed(
            FuelRecord(
                id = 1, vehicleId = vehicleId,
                date = LocalDate.of(2026, 7, 1),
                odometer = 10000, volume = 30.0, amount = 5000,
            ),
        )
        val viewModel = createViewModel()

        // Act（実行）
        viewModel.accept(VehicleDetailIntent.ExportFuelCsv)

        // Assert（検証）
        viewModel.effects.test {
            val effect = awaitItem()
            assertTrue(effect is VehicleDetailEffect.SaveDocument)
            val doc = (effect as VehicleDetailEffect.SaveDocument).document
            assertTrue(doc.fileName.startsWith("fuel_records_"))
            assertEquals("text/csv", doc.mimeType)
            assertTrue(doc.content.contains("10000"))
        }
    }

    @Test
    fun `整備CSVエクスポートでSaveDocumentエフェクトが発行される`() = runTest {
        // Arrange（準備）
        maintenanceRecordRepository.seed(
            MaintenanceRecord(
                id = 1, vehicleId = vehicleId,
                date = LocalDate.of(2026, 7, 1),
                category = MaintenanceCategory.OIL,
                title = "オイル交換", cost = 3000,
            ),
        )
        val viewModel = createViewModel()

        // Act（実行）
        viewModel.accept(VehicleDetailIntent.ExportMaintenanceCsv)

        // Assert（検証）
        viewModel.effects.test {
            val effect = awaitItem()
            assertTrue(effect is VehicleDetailEffect.SaveDocument)
            val doc = (effect as VehicleDetailEffect.SaveDocument).document
            assertTrue(doc.fileName.startsWith("maintenance_records_"))
            assertTrue(doc.content.contains("オイル交換"))
        }
    }

    @Test
    fun `費用CSVエクスポートでSaveDocumentエフェクトが発行される`() = runTest {
        // Arrange（準備）
        costRecordRepository.seed(
            CostRecord(
                id = 1, vehicleId = vehicleId,
                date = LocalDate.of(2026, 7, 1),
                category = CostCategory.FUEL,
                title = "給油", amount = 5000,
            ),
        )
        val viewModel = createViewModel()

        // Act（実行）
        viewModel.accept(VehicleDetailIntent.ExportCostCsv)

        // Assert（検証）
        viewModel.effects.test {
            val effect = awaitItem()
            assertTrue(effect is VehicleDetailEffect.SaveDocument)
            val doc = (effect as VehicleDetailEffect.SaveDocument).document
            assertTrue(doc.fileName.startsWith("cost_records_"))
            assertTrue(doc.content.contains("給油"))
        }
    }

    @Test
    fun `車両がnullの場合フィールドエディタは開かない`() = runTest {
        // Arrange（準備）— 車両をシードしない
        vehicleRepository = FakeVehicleRepository()
        val viewModel = VehicleDetailViewModel(
            vehicleId = 999L,
            vehicleRepository = vehicleRepository,
            fuelRecordRepository = fuelRecordRepository,
            maintenanceRecordRepository = maintenanceRecordRepository,
            costRecordRepository = costRecordRepository,
        )

        // Act（実行）
        viewModel.accept(VehicleDetailIntent.OpenFieldEditor(VehicleField.MAKER))

        // Assert（検証）
        assertNull(viewModel.uiState.value.editingField)
    }

    @Test
    fun `車両がnullの場合SaveFieldは何もしない`() = runTest {
        // Arrange（準備）— 車両をシードしない
        vehicleRepository = FakeVehicleRepository()
        val viewModel = VehicleDetailViewModel(
            vehicleId = 999L,
            vehicleRepository = vehicleRepository,
            fuelRecordRepository = fuelRecordRepository,
            maintenanceRecordRepository = maintenanceRecordRepository,
            costRecordRepository = costRecordRepository,
        )

        // Act（実行）
        viewModel.accept(VehicleDetailIntent.SaveField)

        // Assert（検証）— 例外が発生しないこと
        assertNull(viewModel.uiState.value.vehicle)
    }
}
