package com.segnities007.stylish_myvehicles.presentation.screen.recordslist

import com.segnities007.stylish_myvehicles.domain.model.CostCategory
import com.segnities007.stylish_myvehicles.domain.model.CostRecord
import com.segnities007.stylish_myvehicles.domain.model.FuelRecord
import com.segnities007.stylish_myvehicles.domain.model.MaintenanceCategory
import com.segnities007.stylish_myvehicles.domain.model.MaintenanceRecord
import com.segnities007.stylish_myvehicles.domain.model.Vehicle
import com.segnities007.stylish_myvehicles.domain.model.VehicleCategory
import com.segnities007.stylish_myvehicles.test.FakeCostRecordRepository
import com.segnities007.stylish_myvehicles.test.FakeFuelRecordRepository
import com.segnities007.stylish_myvehicles.test.FakeMaintenanceRecordRepository
import com.segnities007.stylish_myvehicles.test.FakeTripRecordRepository
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RecordsListViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var vehicleRepository: FakeVehicleRepository
    private lateinit var fuelRecordRepository: FakeFuelRecordRepository
    private lateinit var maintenanceRecordRepository: FakeMaintenanceRecordRepository
    private lateinit var costRecordRepository: FakeCostRecordRepository
    private lateinit var tripRecordRepository: FakeTripRecordRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        vehicleRepository = FakeVehicleRepository()
        fuelRecordRepository = FakeFuelRecordRepository()
        maintenanceRecordRepository = FakeMaintenanceRecordRepository()
        costRecordRepository = FakeCostRecordRepository()
        tripRecordRepository = FakeTripRecordRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): RecordsListViewModel =
        RecordsListViewModel(
            vehicleRepository = vehicleRepository,
            fuelRecordRepository = fuelRecordRepository,
            maintenanceRecordRepository = maintenanceRecordRepository,
            costRecordRepository = costRecordRepository,
            tripRecordRepository = tripRecordRepository,
        )

    @Test
    fun `全車両の記録が日付順で統合される`() = runTest {
        // Arrange（準備）
        val vehicle1 = Vehicle(
            id = 1L,
            maker = "トヨタ",
            name = "カローラ",
            category = VehicleCategory.CAR,
        )
        val vehicle2 = Vehicle(
            id = 2L,
            maker = "ホンダ",
            name = "フィット",
            category = VehicleCategory.CAR,
        )
        vehicleRepository.seed(vehicle1, vehicle2)

        fuelRecordRepository.seed(
            FuelRecord(
                id = 1L,
                vehicleId = 1L,
                date = LocalDate.of(2026, 7, 10),
                odometer = 10000,
                volume = 30.0,
                amount = 5000,
            ),
        )
        maintenanceRecordRepository.seed(
            MaintenanceRecord(
                id = 1L,
                vehicleId = 2L,
                date = LocalDate.of(2026, 7, 20),
                category = MaintenanceCategory.OIL,
                title = "オイル交換",
                cost = 3000,
            ),
        )

        // Act（実行）
        val viewModel = createViewModel()

        // Assert（検証）
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        val allEntries = state.sections.flatMap { it.entries }
        assertEquals(2, allEntries.size)
        // 日付降順（新しい順）でソートされている
        assertEquals(LocalDate.of(2026, 7, 20), allEntries[0].date)
        assertEquals(LocalDate.of(2026, 7, 10), allEntries[1].date)
    }

    @Test
    fun `費用の二重計上が除外される（FUELとMAINTENANCEカテゴリ）`() = runTest {
        // Arrange（準備）
        val vehicle = Vehicle(
            id = 1L,
            maker = "トヨタ",
            name = "カローラ",
            category = VehicleCategory.CAR,
        )
        vehicleRepository.seed(vehicle)

        costRecordRepository.seed(
            CostRecord(
                id = 1L,
                vehicleId = 1L,
                date = LocalDate.of(2026, 7, 1),
                category = CostCategory.FUEL,
                title = "給油（自動作成）",
                amount = 5000,
            ),
            CostRecord(
                id = 2L,
                vehicleId = 1L,
                date = LocalDate.of(2026, 7, 5),
                category = CostCategory.MAINTENANCE,
                title = "整備（自動作成）",
                amount = 3000,
            ),
            CostRecord(
                id = 3L,
                vehicleId = 1L,
                date = LocalDate.of(2026, 7, 10),
                category = CostCategory.INSURANCE,
                title = "任意保険",
                amount = 80000,
            ),
        )

        // Act（実行）
        val viewModel = createViewModel()

        // Assert（検証）
        val state = viewModel.uiState.value
        val costEntries = state.sections.flatMap { it.entries }.filterIsInstance<CostEntry>()
        // FUEL と MAINTENANCE カテゴリは除外され、INSURANCE のみ残る
        assertEquals(1, costEntries.size)
        assertEquals(CostCategory.INSURANCE, costEntries[0].record.category)
        assertEquals("任意保険", costEntries[0].record.title)
    }

    @Test
    fun `車両がない場合は空のリストが返る`() = runTest {
        // Arrange（準備）— 車両をシードしない

        // Act（実行）
        val viewModel = createViewModel()

        // Assert（検証）
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertTrue(state.sections.isEmpty())
        assertEquals(0, state.totalCount)
    }
}
