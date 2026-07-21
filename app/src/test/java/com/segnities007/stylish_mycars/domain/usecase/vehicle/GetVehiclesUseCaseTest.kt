package com.segnities007.stylish_mycars.domain.usecase.vehicle

import com.segnities007.stylish_mycars.domain.model.Vehicle
import com.segnities007.stylish_mycars.domain.repository.VehicleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetVehiclesUseCaseTest {
    private val fakeVehicles = listOf(
        Vehicle(id = 1, maker = "トヨタ", name = "プリウス"),
        Vehicle(id = 2, maker = "ホンダ", name = "N-BOX"),
    )

    private val fakeRepository = object : VehicleRepository {
        override fun getAll(): Flow<List<Vehicle>> = flowOf(fakeVehicles)
        override fun getById(id: Long): Flow<Vehicle?> =
            flowOf(fakeVehicles.find { it.id == id })
        override suspend fun insert(vehicle: Vehicle): Long = vehicle.id
        override suspend fun update(vehicle: Vehicle) {}
        override suspend fun delete(vehicle: Vehicle) {}
    }

    @Test
    fun `invoke returns all vehicles`() = runTest {
        val useCase = GetVehiclesUseCase(fakeRepository)
        val result = useCase().first()
        assertEquals(2, result.size)
        assertEquals("プリウス", result[0].name)
    }

    @Test
    fun `invoke returns empty list when no vehicles`() = runTest {
        val emptyRepository = object : VehicleRepository {
            override fun getAll(): Flow<List<Vehicle>> = flowOf(emptyList())
            override fun getById(id: Long): Flow<Vehicle?> = flowOf(null)
            override suspend fun insert(vehicle: Vehicle): Long = 0
            override suspend fun update(vehicle: Vehicle) {}
            override suspend fun delete(vehicle: Vehicle) {}
        }
        val useCase = GetVehiclesUseCase(emptyRepository)
        val result = useCase().first()
        assertEquals(0, result.size)
    }
}
