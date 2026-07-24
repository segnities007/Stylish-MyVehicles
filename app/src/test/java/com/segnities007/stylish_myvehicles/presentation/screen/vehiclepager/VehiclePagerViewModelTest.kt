package com.segnities007.stylish_myvehicles.presentation.screen.vehiclepager

import app.cash.turbine.test
import com.segnities007.stylish_myvehicles.domain.model.CostCategory
import com.segnities007.stylish_myvehicles.domain.model.CostRecord
import com.segnities007.stylish_myvehicles.domain.model.Vehicle
import com.segnities007.stylish_myvehicles.domain.model.VehicleCategory
import com.segnities007.stylish_myvehicles.test.FakeCostRecordRepository
import com.segnities007.stylish_myvehicles.test.FakeFuelRecordRepository
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class VehiclePagerViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var vehicleRepository: FakeVehicleRepository
    private lateinit var costRecordRepository: FakeCostRecordRepository
    private lateinit var fuelRecordRepository: FakeFuelRecordRepository
    private lateinit var scheduleRepository: FakeMaintenanceScheduleRepository

    private val vehicle1 = Vehicle(
        id = 1L,
        maker = "トヨタ",
        name = "カローラ",
        category = VehicleCategory.CAR,
    )
    private val vehicle2 = Vehicle(
        id = 2L,
        maker = "ホンダ",
        name = "フィット",
        category = VehicleCategory.CAR,
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        vehicleRepository = FakeVehicleRepository()
        costRecordRepository = FakeCostRecordRepository()
        fuelRecordRepository = FakeFuelRecordRepository()
        scheduleRepository = FakeMaintenanceScheduleRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): VehiclePagerViewModel =
        VehiclePagerViewModel(
            vehicleRepository = vehicleRepository,
            costRecordRepository = costRecordRepository,
            fuelRecordRepository = fuelRecordRepository,
            maintenanceScheduleRepository = scheduleRepository,
        )

    @Test
    fun `初期状態で車両リストが読み込まれる`() = runTest {
        // Arrange（準備）
        vehicleRepository.seed(vehicle1, vehicle2)

        // Act（実行）
        val viewModel = createViewModel()

        // Assert（検証）
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(2, state.vehicles.size)
        assertEquals("カローラ", state.vehicles[0].name)
        assertEquals("フィット", state.vehicles[1].name)
    }

    @Test
    fun `ページ変更でcurrentPageが更新される`() = runTest {
        // Arrange（準備）
        vehicleRepository.seed(vehicle1, vehicle2)
        val viewModel = createViewModel()

        // Act（実行）
        viewModel.accept(VehiclePagerIntent.PageChanged(1))

        // Assert（検証）
        assertEquals(1, viewModel.uiState.value.currentPage)
        assertEquals("フィット", viewModel.uiState.value.currentVehicle?.name)
    }

    @Test
    fun `車両追加ナビゲーション効果が送信される`() = runTest {
        // Arrange（準備）
        vehicleRepository.seed(vehicle1)
        val viewModel = createViewModel()

        // Act（実行）& Assert（検証）
        viewModel.effects.test {
            viewModel.accept(VehiclePagerIntent.AddVehicle)
            val effect = awaitItem()
            assertEquals(VehiclePagerEffect.NavigateToEdit(null), effect)
        }
    }

    @Test
    fun `ダッシュボードが車両ごとに計算される`() = runTest {
        // Arrange（準備）
        vehicleRepository.seed(vehicle1)
        val now = LocalDate.now()
        costRecordRepository.seed(
            CostRecord(
                id = 1L,
                vehicleId = 1L,
                date = now,
                category = CostCategory.FUEL,
                title = "給油",
                amount = 5000,
            ),
            CostRecord(
                id = 2L,
                vehicleId = 1L,
                date = now.minusMonths(1),
                category = CostCategory.INSURANCE,
                title = "保険",
                amount = 80000,
            ),
        )

        // Act（実行）
        val viewModel = createViewModel()

        // Assert（検証）
        val state = viewModel.uiState.value
        assertTrue(state.dashboardByVehicle.containsKey(1L))
        val dashboard = state.dashboardFor(1L)
        assertEquals(85000, dashboard.totalCost)
    }
}
