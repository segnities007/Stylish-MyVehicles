package com.segnities007.stylish_myvehicles.presentation.screen.vehiclelist

import app.cash.turbine.test
import com.segnities007.stylish_myvehicles.domain.model.Vehicle
import com.segnities007.stylish_myvehicles.domain.model.VehicleCategory
import com.segnities007.stylish_myvehicles.test.FakeVehicleRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class VehicleListViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var vehicleRepository: FakeVehicleRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        vehicleRepository = FakeVehicleRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): VehicleListViewModel =
        VehicleListViewModel(vehicleRepository = vehicleRepository)

    @Test
    fun `初期状態で車両リストが読み込まれる`() = runTest {
        // Arrange（準備）
        vehicleRepository.seed(
            Vehicle(id = 1L, maker = "トヨタ", name = "カローラ", category = VehicleCategory.CAR),
            Vehicle(id = 2L, maker = "ホンダ", name = "フィット", category = VehicleCategory.CAR),
            Vehicle(id = 3L, maker = "ヤマハ", name = "MT-07", category = VehicleCategory.MOTORCYCLE),
        )

        // Act（実行）
        val viewModel = createViewModel()

        // Assert（検証）
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(3, state.vehicles.size)
        assertEquals("カローラ", state.vehicles[0].name)
        assertEquals("フィット", state.vehicles[1].name)
        assertEquals("MT-07", state.vehicles[2].name)
    }

    @Test
    fun `車両選択でナビゲーション効果が送信される`() = runTest {
        // Arrange（準備）
        vehicleRepository.seed(
            Vehicle(id = 1L, maker = "トヨタ", name = "カローラ", category = VehicleCategory.CAR),
        )
        val viewModel = createViewModel()

        // Act（実行）& Assert（検証）
        viewModel.effects.test {
            viewModel.accept(VehicleListIntent.SelectVehicle(vehicleId = 1L))
            val effect = awaitItem()
            assertEquals(VehicleListEffect.NavigateToDetail(1L), effect)
        }
    }
}
