package com.segnities007.stylish_myvehicles.presentation.screen.records

import app.cash.turbine.test
import com.segnities007.stylish_myvehicles.domain.model.CostCategory
import com.segnities007.stylish_myvehicles.domain.model.CostRecord
import com.segnities007.stylish_myvehicles.domain.model.FuelRecord
import com.segnities007.stylish_myvehicles.domain.model.MaintenanceCategory
import com.segnities007.stylish_myvehicles.domain.model.MaintenanceRecord
import com.segnities007.stylish_myvehicles.domain.model.Vehicle
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RecordsViewModelTest {

    private lateinit var fuelRepo: FakeFuelRecordRepository
    private lateinit var maintenanceRepo: FakeMaintenanceRecordRepository
    private lateinit var costRepo: FakeCostRecordRepository
    private lateinit var vehicleRepo: FakeVehicleRepository

    private val vehicle = Vehicle(id = 1, maker = "トヨタ", name = "プリウス")

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        fuelRepo = FakeFuelRecordRepository()
        maintenanceRepo = FakeMaintenanceRecordRepository()
        costRepo = FakeCostRecordRepository()
        vehicleRepo = FakeVehicleRepository()
        vehicleRepo.seed(vehicle)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(
        vehicleId: Long = 1L,
        topic: RecordTopic = RecordTopic.FUEL,
        periodMode: PeriodMode = PeriodMode.MONTHLY,
    ) = RecordsViewModel(
        vehicleId = vehicleId,
        topic = topic,
        initialPeriodMode = periodMode,
        fuelRecordRepository = fuelRepo,
        maintenanceRecordRepository = maintenanceRepo,
        costRecordRepository = costRepo,
        vehicleRepository = vehicleRepo,
    )

    @Test
    fun `FUELトピックで給油記録が読み込まれる`() = runTest {
        // Arrange（準備）
        fuelRepo.seed(
            FuelRecord(
                id = 1, vehicleId = 1, date = LocalDate.now().minusDays(2),
                odometer = 10000, volume = 30.0, amount = 5000,
            ),
            FuelRecord(
                id = 2, vehicleId = 1, date = LocalDate.now(),
                odometer = 10500, volume = 25.0, amount = 4200,
            ),
        )

        // Act（実行）
        val vm = createViewModel(topic = RecordTopic.FUEL)

        // Assert（検証）
        vm.uiState.test {
            val state = awaitItem()
            assertEquals(2, state.fuelRecords.size)
            assertFalse(state.isLoading)
            assertEquals(RecordTopic.FUEL, state.topic)
            assertNotNull(state.vehicle)
            assertEquals("プリウス", state.vehicle?.name)
        }
    }

    @Test
    fun `MAINTENANCEトピックで整備記録が読み込まれる`() = runTest {
        // Arrange（準備）
        maintenanceRepo.seed(
            MaintenanceRecord(
                id = 1, vehicleId = 1, date = LocalDate.now(),
                category = MaintenanceCategory.OIL, title = "オイル交換", cost = 3000,
            ),
        )

        // Act（実行）
        val vm = createViewModel(topic = RecordTopic.MAINTENANCE)

        // Assert（検証）
        vm.uiState.test {
            val state = awaitItem()
            assertEquals(1, state.maintenanceRecords.size)
            assertEquals("オイル交換", state.maintenanceRecords.first().title)
            assertEquals(RecordTopic.MAINTENANCE, state.topic)
        }
    }

    @Test
    fun `COSTトピックで費用記録が読み込まれる`() = runTest {
        // Arrange（準備）
        costRepo.seed(
            CostRecord(
                id = 1, vehicleId = 1, date = LocalDate.now(),
                category = CostCategory.FUEL, title = "ガソリン代", amount = 5000,
            ),
            CostRecord(
                id = 2, vehicleId = 1, date = LocalDate.now(),
                category = CostCategory.PARKING, title = "駐車場代", amount = 8000,
            ),
        )

        // Act（実行）
        val vm = createViewModel(topic = RecordTopic.COST)

        // Assert（検証）
        vm.uiState.test {
            val state = awaitItem()
            assertEquals(2, state.costRecords.size)
            assertEquals(RecordTopic.COST, state.topic)
        }
    }

    @Test
    fun `期間変更でフィルターが更新される`() = runTest {
        // Arrange（準備）
        fuelRepo.seed(
            FuelRecord(
                id = 1, vehicleId = 1, date = LocalDate.now(),
                odometer = 10000, volume = 30.0, amount = 5000,
            ),
        )
        val vm = createViewModel(topic = RecordTopic.FUEL, periodMode = PeriodMode.MONTHLY)

        // Act（実行）
        vm.accept(RecordsIntent.ChangePeriodMode(PeriodMode.YEARLY))

        // Assert（検証）
        vm.uiState.test {
            val state = awaitItem()
            assertEquals(PeriodMode.YEARLY, state.periodMode)
            // 年次モードでは年単位のページが生成される
            assertTrue(state.periods.all { it.label.endsWith("年") })
        }
    }

    @Test
    fun `月次期間のリストが正しく生成される`() = runTest {
        // Arrange（準備）
        val threeMonthsAgo = LocalDate.now().minusMonths(3)
        fuelRepo.seed(
            FuelRecord(
                id = 1, vehicleId = 1, date = threeMonthsAgo,
                odometer = 9000, volume = 30.0, amount = 5000,
            ),
            FuelRecord(
                id = 2, vehicleId = 1, date = LocalDate.now(),
                odometer = 10000, volume = 25.0, amount = 4000,
            ),
        )

        // Act（実行）
        val vm = createViewModel(topic = RecordTopic.FUEL, periodMode = PeriodMode.MONTHLY)

        // Assert（検証）
        vm.uiState.test {
            val state = awaitItem()
            // 3ヶ月前から今月まで＋前月から始まるので4ページ以上
            assertTrue(state.periods.size >= 4)
            // 昇順（古い→新しい）
            val starts = state.periods.map { it.start }
            assertEquals(starts.sorted(), starts)
        }
    }

    @Test
    fun `ページ変更でcurrentPageが更新される`() = runTest {
        // Arrange（準備）
        fuelRepo.seed(
            FuelRecord(
                id = 1, vehicleId = 1, date = LocalDate.now().minusMonths(2),
                odometer = 9000, volume = 30.0, amount = 5000,
            ),
        )
        val vm = createViewModel(topic = RecordTopic.FUEL)

        // Act（実行）
        vm.accept(RecordsIntent.PageChanged(1))

        // Assert（検証）
        assertEquals(1, vm.uiState.value.currentPage)
    }

    @Test
    fun `NavigateBackでエフェクトが発行される`() = runTest {
        // Arrange（準備）
        val vm = createViewModel()

        // Act（実行）
        vm.accept(RecordsIntent.NavigateBack)

        // Assert（検証）
        vm.effects.test {
            assertEquals(RecordsEffect.NavigateBack, awaitItem())
        }
    }

    @Test
    fun `他車両の記録は含まれない`() = runTest {
        // Arrange（準備）
        fuelRepo.seed(
            FuelRecord(
                id = 1, vehicleId = 1, date = LocalDate.now(),
                odometer = 10000, volume = 30.0, amount = 5000,
            ),
            FuelRecord(
                id = 2, vehicleId = 99, date = LocalDate.now(),
                odometer = 20000, volume = 40.0, amount = 6000,
            ),
        )

        // Act（実行）
        val vm = createViewModel(vehicleId = 1L, topic = RecordTopic.FUEL)

        // Assert（検証）
        vm.uiState.test {
            val state = awaitItem()
            assertEquals(1, state.fuelRecords.size)
            assertEquals(1L, state.fuelRecords.first().vehicleId)
        }
    }
}
