package com.segnities007.stylish_myvehicles.test

import com.segnities007.stylish_myvehicles.domain.model.CostRecord
import com.segnities007.stylish_myvehicles.domain.model.FuelRecord
import com.segnities007.stylish_myvehicles.domain.model.MaintenanceRecord
import com.segnities007.stylish_myvehicles.domain.model.MaintenanceSchedule
import com.segnities007.stylish_myvehicles.domain.model.TripRecord
import com.segnities007.stylish_myvehicles.domain.model.Vehicle
import com.segnities007.stylish_myvehicles.domain.model.VehicleCategory
import com.segnities007.stylish_myvehicles.domain.repository.CostRecordRepository
import com.segnities007.stylish_myvehicles.domain.repository.FuelRecordRepository
import com.segnities007.stylish_myvehicles.domain.repository.MaintenanceRecordRepository
import com.segnities007.stylish_myvehicles.domain.repository.MaintenanceScheduleRepository
import com.segnities007.stylish_myvehicles.domain.repository.TripRecordRepository
import com.segnities007.stylish_myvehicles.domain.repository.VehicleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import java.time.LocalDate

class FakeVehicleRepository : VehicleRepository {
    private val store = MutableStateFlow<List<Vehicle>>(emptyList())
    private var nextId = 1L

    override fun getAll(): Flow<List<Vehicle>> = store

    override fun getById(id: Long): Flow<Vehicle?> =
        store.map { list -> list.find { it.id == id } }

    override suspend fun insert(vehicle: Vehicle): Long {
        val id = nextId++
        store.update { it + vehicle.copy(id = id) }
        return id
    }

    override suspend fun update(vehicle: Vehicle) {
        store.update { list -> list.map { if (it.id == vehicle.id) vehicle else it } }
    }

    override suspend fun delete(vehicle: Vehicle) {
        store.update { list -> list.filter { it.id != vehicle.id } }
    }

    override suspend fun deleteById(id: Long) {
        store.update { list -> list.filter { it.id != id } }
    }

    fun seed(vararg vehicles: Vehicle) {
        store.value = vehicles.toList()
        nextId = (vehicles.maxOfOrNull { it.id } ?: 0) + 1
    }
}

class FakeFuelRecordRepository : FuelRecordRepository {
    private val store = MutableStateFlow<List<FuelRecord>>(emptyList())
    private var nextId = 1L

    override fun getByVehicleId(vehicleId: Long): Flow<List<FuelRecord>> =
        store.map { list -> list.filter { it.vehicleId == vehicleId } }

    override suspend fun getLatest(vehicleId: Long): FuelRecord? =
        store.value.filter { it.vehicleId == vehicleId }
            .maxByOrNull { it.date }

    override suspend fun insert(record: FuelRecord): Long {
        val id = nextId++
        store.update { it + record.copy(id = id) }
        return id
    }

    override suspend fun update(record: FuelRecord) {
        store.update { list -> list.map { if (it.id == record.id) record else it } }
    }

    override suspend fun delete(record: FuelRecord) {
        store.update { list -> list.filter { it.id != record.id } }
    }

    override suspend fun deleteById(id: Long) {
        store.update { list -> list.filter { it.id != id } }
    }

    fun seed(vararg records: FuelRecord) {
        store.value = records.toList()
        nextId = (records.maxOfOrNull { it.id } ?: 0) + 1
    }
}

class FakeMaintenanceRecordRepository : MaintenanceRecordRepository {
    private val store = MutableStateFlow<List<MaintenanceRecord>>(emptyList())
    private var nextId = 1L

    override fun getByVehicleId(vehicleId: Long): Flow<List<MaintenanceRecord>> =
        store.map { list -> list.filter { it.vehicleId == vehicleId } }

    override suspend fun insert(record: MaintenanceRecord): Long {
        val id = nextId++
        store.update { it + record.copy(id = id) }
        return id
    }

    override suspend fun update(record: MaintenanceRecord) {
        store.update { list -> list.map { if (it.id == record.id) record else it } }
    }

    override suspend fun delete(record: MaintenanceRecord) {
        store.update { list -> list.filter { it.id != record.id } }
    }

    fun seed(vararg records: MaintenanceRecord) {
        store.value = records.toList()
        nextId = (records.maxOfOrNull { it.id } ?: 0) + 1
    }
}

class FakeMaintenanceScheduleRepository : MaintenanceScheduleRepository {
    private val store = MutableStateFlow<List<MaintenanceSchedule>>(emptyList())
    val insertedDefaults = mutableListOf<Pair<Long, VehicleCategory>>()
    val updatedLastDone = mutableListOf<Tuple4>()

    data class Tuple4(val vehicleId: Long, val category: String, val date: LocalDate, val odometer: Int?)

    override fun getByVehicleId(vehicleId: Long): Flow<List<MaintenanceSchedule>> =
        store.map { list -> list.filter { it.vehicleId == vehicleId } }

    override suspend fun getAll(): List<MaintenanceSchedule> = store.value

    override suspend fun insertDefaults(vehicleId: Long, category: VehicleCategory) {
        insertedDefaults.add(vehicleId to category)
    }

    override suspend fun update(schedule: MaintenanceSchedule) {
        store.update { list -> list.map { if (it.id == schedule.id) schedule else it } }
    }

    override suspend fun updateLastDone(vehicleId: Long, category: String, date: LocalDate, odometer: Int?) {
        updatedLastDone.add(Tuple4(vehicleId, category, date, odometer))
    }

    fun seed(vararg schedules: MaintenanceSchedule) {
        store.value = schedules.toList()
    }
}

class FakeCostRecordRepository : CostRecordRepository {
    private val store = MutableStateFlow<List<CostRecord>>(emptyList())
    private var nextId = 1L

    override fun getByVehicleId(vehicleId: Long): Flow<List<CostRecord>> =
        store.map { list -> list.filter { it.vehicleId == vehicleId } }

    override fun getByVehicleIdAndDateRange(
        vehicleId: Long,
        start: LocalDate,
        end: LocalDate,
    ): Flow<List<CostRecord>> =
        store.map { list ->
            list.filter { it.vehicleId == vehicleId && it.date in start..end }
        }

    override suspend fun insert(record: CostRecord): Long {
        val id = nextId++
        store.update { it + record.copy(id = id) }
        return id
    }

    override suspend fun update(record: CostRecord) {
        store.update { list -> list.map { if (it.id == record.id) record else it } }
    }

    override suspend fun delete(record: CostRecord) {
        store.update { list -> list.filter { it.id != record.id } }
    }

    fun seed(vararg records: CostRecord) {
        store.value = records.toList()
        nextId = (records.maxOfOrNull { it.id } ?: 0) + 1
    }

    fun all(): List<CostRecord> = store.value
}

class FakeTripRecordRepository : TripRecordRepository {
    private val store = MutableStateFlow<List<TripRecord>>(emptyList())
    private var nextId = 1L

    override fun getByVehicleId(vehicleId: Long): Flow<List<TripRecord>> =
        store.map { list -> list.filter { it.vehicleId == vehicleId } }

    override suspend fun insert(record: TripRecord): Long {
        val id = nextId++
        store.update { it + record.copy(id = id) }
        return id
    }

    override suspend fun update(record: TripRecord) {
        store.update { list -> list.map { if (it.id == record.id) record else it } }
    }

    override suspend fun delete(record: TripRecord) {
        store.update { list -> list.filter { it.id != record.id } }
    }

    fun seed(vararg records: TripRecord) {
        store.value = records.toList()
        nextId = (records.maxOfOrNull { it.id } ?: 0) + 1
    }
}
