package com.segnities007.stylish_myvehicles.domain.usecase.vehicle

import com.segnities007.stylish_myvehicles.domain.model.Vehicle
import com.segnities007.stylish_myvehicles.domain.model.VehicleCategory
import com.segnities007.stylish_myvehicles.test.FakeMaintenanceScheduleRepository
import com.segnities007.stylish_myvehicles.test.FakeVehicleRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class InsertVehicleUseCaseTest {
    private lateinit var vehicleRepository: FakeVehicleRepository
    private lateinit var scheduleRepository: FakeMaintenanceScheduleRepository
    private lateinit var useCase: InsertVehicleUseCase

    @Before
    fun setUp() {
        vehicleRepository = FakeVehicleRepository()
        scheduleRepository = FakeMaintenanceScheduleRepository()
        useCase = InsertVehicleUseCase(vehicleRepository, scheduleRepository)
    }

    @Test
    fun `車両を保存するとデフォルトの整備スケジュールが作成される`() = runTest {
        // Arrange（準備）
        val vehicle = Vehicle(
            maker = "トヨタ",
            name = "カローラ",
            category = VehicleCategory.CAR,
        )

        // Act（実行）
        val id = useCase(vehicle)

        // Assert（検証）
        assertEquals(1, scheduleRepository.insertedDefaults.size)
        val (vehicleId, category) = scheduleRepository.insertedDefaults.first()
        assertEquals(id, vehicleId)
        assertEquals(VehicleCategory.CAR, category)
    }

    @Test
    fun `車両のIDが返される`() = runTest {
        // Arrange（準備）
        val vehicle = Vehicle(
            maker = "ホンダ",
            name = "フィット",
            category = VehicleCategory.CAR,
        )

        // Act（実行）
        val id = useCase(vehicle)

        // Assert（検証）
        assertTrue(id > 0)
    }

    @Test
    fun `車両カテゴリに応じたスケジュールが作成される`() = runTest {
        // Arrange（準備）
        val motorcycle = Vehicle(
            maker = "ヤマハ",
            name = "MT-07",
            category = VehicleCategory.MOTORCYCLE,
        )
        val bicycle = Vehicle(
            maker = "ジャイアント",
            name = "ESCAPE",
            category = VehicleCategory.BICYCLE,
        )

        // Act（実行）
        useCase(motorcycle)
        useCase(bicycle)

        // Assert（検証）
        assertEquals(2, scheduleRepository.insertedDefaults.size)
        assertEquals(VehicleCategory.MOTORCYCLE, scheduleRepository.insertedDefaults[0].second)
        assertEquals(VehicleCategory.BICYCLE, scheduleRepository.insertedDefaults[1].second)
    }
}
