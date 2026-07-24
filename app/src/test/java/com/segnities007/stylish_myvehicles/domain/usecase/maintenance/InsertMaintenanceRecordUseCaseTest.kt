package com.segnities007.stylish_myvehicles.domain.usecase.maintenance

import com.segnities007.stylish_myvehicles.domain.model.CostCategory
import com.segnities007.stylish_myvehicles.domain.model.MaintenanceCategory
import com.segnities007.stylish_myvehicles.domain.model.MaintenanceRecord
import com.segnities007.stylish_myvehicles.test.FakeCostRecordRepository
import com.segnities007.stylish_myvehicles.test.FakeMaintenanceRecordRepository
import com.segnities007.stylish_myvehicles.test.FakeMaintenanceScheduleRepository
import java.time.LocalDate
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class InsertMaintenanceRecordUseCaseTest {
    private lateinit var maintenanceRecordRepository: FakeMaintenanceRecordRepository
    private lateinit var costRecordRepository: FakeCostRecordRepository
    private lateinit var scheduleRepository: FakeMaintenanceScheduleRepository
    private lateinit var useCase: InsertMaintenanceRecordUseCase

    @Before
    fun setUp() {
        maintenanceRecordRepository = FakeMaintenanceRecordRepository()
        costRecordRepository = FakeCostRecordRepository()
        scheduleRepository = FakeMaintenanceScheduleRepository()
        useCase = InsertMaintenanceRecordUseCase(
            maintenanceRecordRepository,
            costRecordRepository,
            scheduleRepository,
        )
    }

    @Test
    fun `整備記録を保存すると費用レコードも自動作成される（費用が0より大きい場合）`() = runTest {
        // Arrange（準備）
        val record = MaintenanceRecord(
            vehicleId = 1L,
            date = LocalDate.of(2026, 7, 20),
            category = MaintenanceCategory.OIL,
            title = "エンジンオイル交換",
            cost = 5000,
            odometer = 30000,
        )

        // Act（実行）
        useCase(record)

        // Assert（検証）
        val costRecords = costRecordRepository.all()
        assertEquals(1, costRecords.size)
        val costRecord = costRecords.first()
        assertEquals(1L, costRecord.vehicleId)
        assertEquals(LocalDate.of(2026, 7, 20), costRecord.date)
        assertEquals(CostCategory.MAINTENANCE, costRecord.category)
        assertEquals("エンジンオイル交換", costRecord.title)
        assertEquals(5000, costRecord.amount)
    }

    @Test
    fun `費用が0の場合は費用レコードが作成されない`() = runTest {
        // Arrange（準備）
        val record = MaintenanceRecord(
            vehicleId = 1L,
            date = LocalDate.of(2026, 7, 20),
            category = MaintenanceCategory.WIPER,
            title = "ワイパー点検",
            cost = 0,
        )

        // Act（実行）
        useCase(record)

        // Assert（検証）
        assertTrue(costRecordRepository.all().isEmpty())
    }

    @Test
    fun `整備スケジュールのlastDoneが更新される`() = runTest {
        // Arrange（準備）
        val record = MaintenanceRecord(
            vehicleId = 1L,
            date = LocalDate.of(2026, 7, 20),
            category = MaintenanceCategory.OIL,
            title = "エンジンオイル交換",
            cost = 3000,
            odometer = 50000,
        )

        // Act（実行）
        useCase(record)

        // Assert（検証）
        assertEquals(1, scheduleRepository.updatedLastDone.size)
        val updated = scheduleRepository.updatedLastDone.first()
        assertEquals(1L, updated.vehicleId)
        assertEquals("OIL", updated.category)
        assertEquals(LocalDate.of(2026, 7, 20), updated.date)
        assertEquals(50000, updated.odometer)
    }

    @Test
    fun `記録のIDが返される`() = runTest {
        // Arrange（準備）
        val record = MaintenanceRecord(
            vehicleId = 1L,
            date = LocalDate.of(2026, 7, 20),
            category = MaintenanceCategory.TIRE,
            title = "タイヤ交換",
            cost = 40000,
        )

        // Act（実行）
        val id = useCase(record)

        // Assert（検証）
        assertTrue(id > 0)
    }
}
