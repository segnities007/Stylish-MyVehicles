package com.segnities007.stylish_mycars.domain.usecase.fuel

import com.segnities007.stylish_mycars.domain.model.CostCategory
import com.segnities007.stylish_mycars.domain.model.CostRecord
import com.segnities007.stylish_mycars.domain.model.FuelRecord
import com.segnities007.stylish_mycars.domain.repository.CostRecordRepository
import com.segnities007.stylish_mycars.domain.repository.FuelRecordRepository
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class InsertFuelRecordUseCaseTest {
    private val insertedCostRecords = mutableListOf<CostRecord>()

    private val fakeFuelRepository = object : FuelRecordRepository {
        override fun getByVehicleId(vehicleId: Long): Flow<List<FuelRecord>> =
            flowOf(emptyList())
        override suspend fun getLatest(vehicleId: Long): FuelRecord? = null
        override suspend fun insert(record: FuelRecord): Long = 1L
        override suspend fun update(record: FuelRecord) {}
        override suspend fun delete(record: FuelRecord) {}
    }

    private val fakeCostRepository = object : CostRecordRepository {
        override fun getByVehicleId(vehicleId: Long): Flow<List<CostRecord>> =
            flowOf(emptyList())
        override fun getByVehicleIdAndDateRange(
            vehicleId: Long, start: LocalDate, end: LocalDate,
        ): Flow<List<CostRecord>> = flowOf(emptyList())
        override suspend fun insert(record: CostRecord): Long {
            insertedCostRecords.add(record)
            return 1L
        }
        override suspend fun update(record: CostRecord) {}
        override suspend fun delete(record: CostRecord) {}
    }

    @Test
    fun `invoke inserts fuel record and creates cost record`() = runTest {
        val useCase = InsertFuelRecordUseCase(fakeFuelRepository, fakeCostRepository)
        val record = FuelRecord(
            vehicleId = 1,
            date = LocalDate.of(2026, 7, 20),
            odometer = 10000,
            volume = 30.0,
            amount = 5000,
        )

        val id = useCase(record)

        assertEquals(1L, id)
        assertEquals(1, insertedCostRecords.size)
        val costRecord = insertedCostRecords.first()
        assertEquals(CostCategory.FUEL, costRecord.category)
        assertEquals(5000, costRecord.amount)
        assertTrue(costRecord.title.contains("30.0L"))
    }
}
