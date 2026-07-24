package com.segnities007.stylish_myvehicles.presentation.screen.notification

import com.segnities007.stylish_myvehicles.domain.model.MaintenanceCategory
import com.segnities007.stylish_myvehicles.domain.model.MaintenanceSchedule
import com.segnities007.stylish_myvehicles.domain.model.Vehicle
import com.segnities007.stylish_myvehicles.domain.model.VehicleCategory
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
class NotificationViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var vehicleRepository: FakeVehicleRepository
    private lateinit var scheduleRepository: FakeMaintenanceScheduleRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        vehicleRepository = FakeVehicleRepository()
        scheduleRepository = FakeMaintenanceScheduleRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): NotificationViewModel =
        NotificationViewModel(
            vehicleRepository = vehicleRepository,
            maintenanceScheduleRepository = scheduleRepository,
        )

    @Test
    fun `期限切れの項目が正しく表示される`() = runTest {
        // Arrange（準備）
        val now = LocalDate.now()
        val vehicle = Vehicle(
            id = 1L,
            maker = "トヨタ",
            name = "カローラ",
            category = VehicleCategory.CAR,
            jibaiExpiry = now.minusDays(10),
        )
        vehicleRepository.seed(vehicle)

        // Act（実行）
        val viewModel = createViewModel()

        // Assert（検証）
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        val expiredItems = state.deadlines.filter { it.deadline.daysRemaining < 0 }
        assertTrue(expiredItems.isNotEmpty())
        val jibaiItem = expiredItems.find { it.deadline.label == "自賠責保険" }
        assertEquals("カローラ", jibaiItem!!.vehicleName)
        assertTrue(jibaiItem.deadline.isExpired)
    }

    @Test
    fun `期限が近い項目が正しく表示される`() = runTest {
        // Arrange（準備）
        val now = LocalDate.now()
        val vehicle = Vehicle(
            id = 1L,
            maker = "ホンダ",
            name = "フィット",
            category = VehicleCategory.CAR,
            insuranceExpiry = now.plusDays(15),
        )
        vehicleRepository.seed(vehicle)

        // Act（実行）
        val viewModel = createViewModel()

        // Assert（検証）
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        val urgentItems = state.deadlines.filter { it.deadline.daysRemaining in 0..30 }
        assertTrue(urgentItems.isNotEmpty())
        val insuranceItem = urgentItems.find { it.deadline.label == "任意保険" }
        assertEquals("フィット", insuranceItem!!.vehicleName)
        assertTrue(insuranceItem.deadline.isUrgent)
    }

    @Test
    fun `車両がない場合は空のリストが返る`() = runTest {
        // Arrange（準備）— 車両をシードしない

        // Act（実行）
        val viewModel = createViewModel()

        // Assert（検証）
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertTrue(state.deadlines.isEmpty())
    }
}
