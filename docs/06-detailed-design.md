# 詳細設計書 — Stylish MyCars

> 作成日: 2026-07-20
> 参照: [基本設計書](05-basic-design.md)

---

## 1. Data層

### 1.1 Room Entity

#### VehicleEntity

```kotlin
@Entity(tableName = "vehicles")
data class VehicleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val maker: String,
    val name: String,
    val grade: String = "",
    val year: Int? = null,
    val modelCode: String = "",
    val plateNumber: String = "",
    val vin: String = "",
    val displacement: Int? = null,
    val weight: Int? = null,
    val color: String = "",
    val firstRegistrationDate: LocalDate? = null,
    val inspectionExpiry: LocalDate? = null,
    val jibaiExpiry: LocalDate? = null,
    val insuranceExpiry: LocalDate? = null,
    val insuranceCompany: String = "",
    val insuranceRank: Int? = null,
    val taxPaid: Boolean = false,
    val photoUri: String? = null,
    val memo: String = "",
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
)
```

#### FuelRecordEntity

```kotlin
@Entity(
    tableName = "fuel_records",
    foreignKeys = [ForeignKey(
        entity = VehicleEntity::class,
        parentColumns = ["id"],
        childColumns = ["vehicleId"],
        onDelete = ForeignKey.CASCADE,
    )],
    indices = [Index("vehicleId")],
)
data class FuelRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val vehicleId: Long,
    val date: LocalDate,
    val odometer: Int,
    val volume: Double,
    val amount: Int,
    val unitPrice: Int? = null,
    val isFullTank: Boolean = true,
    val memo: String = "",
    val createdAt: LocalDateTime = LocalDateTime.now(),
)
```

#### MaintenanceRecordEntity

```kotlin
@Entity(
    tableName = "maintenance_records",
    foreignKeys = [ForeignKey(
        entity = VehicleEntity::class,
        parentColumns = ["id"],
        childColumns = ["vehicleId"],
        onDelete = ForeignKey.CASCADE,
    )],
    indices = [Index("vehicleId")],
)
data class MaintenanceRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val vehicleId: Long,
    val date: LocalDate,
    val odometer: Int? = null,
    val category: String,       // MaintenanceCategory.name
    val title: String,
    val cost: Int = 0,
    val shopName: String = "",
    val memo: String = "",
    val createdAt: LocalDateTime = LocalDateTime.now(),
)
```

#### CostRecordEntity

```kotlin
@Entity(
    tableName = "cost_records",
    foreignKeys = [ForeignKey(
        entity = VehicleEntity::class,
        parentColumns = ["id"],
        childColumns = ["vehicleId"],
        onDelete = ForeignKey.CASCADE,
    )],
    indices = [Index("vehicleId")],
)
data class CostRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val vehicleId: Long,
    val date: LocalDate,
    val category: String,       // CostCategory.name
    val title: String,
    val amount: Int,
    val memo: String = "",
    val createdAt: LocalDateTime = LocalDateTime.now(),
)
```

### 1.2 Converters

```kotlin
class Converters {
    @TypeConverter
    fun fromLocalDate(value: LocalDate?): String? = value?.toString()

    @TypeConverter
    fun toLocalDate(value: String?): LocalDate? = value?.let { LocalDate.parse(it) }

    @TypeConverter
    fun fromLocalDateTime(value: LocalDateTime?): String? = value?.toString()

    @TypeConverter
    fun toLocalDateTime(value: String?): LocalDateTime? =
        value?.let { LocalDateTime.parse(it) }
}
```

### 1.3 DAO

#### VehicleDao

```kotlin
@Dao
interface VehicleDao {
    @Query("SELECT * FROM vehicles ORDER BY createdAt DESC")
    fun getAll(): Flow<List<VehicleEntity>>

    @Query("SELECT * FROM vehicles WHERE id = :id")
    fun getById(id: Long): Flow<VehicleEntity?>

    @Insert
    suspend fun insert(entity: VehicleEntity): Long

    @Update
    suspend fun update(entity: VehicleEntity)

    @Delete
    suspend fun delete(entity: VehicleEntity)
}
```

#### FuelRecordDao

```kotlin
@Dao
interface FuelRecordDao {
    @Query("SELECT * FROM fuel_records WHERE vehicleId = :vehicleId ORDER BY date DESC, odometer DESC")
    fun getByVehicleId(vehicleId: Long): Flow<List<FuelRecordEntity>>

    @Query("SELECT * FROM fuel_records WHERE vehicleId = :vehicleId ORDER BY date DESC, odometer DESC LIMIT 1")
    suspend fun getLatest(vehicleId: Long): FuelRecordEntity?

    @Insert
    suspend fun insert(entity: FuelRecordEntity): Long

    @Delete
    suspend fun delete(entity: FuelRecordEntity)
}
```

#### MaintenanceRecordDao

```kotlin
@Dao
interface MaintenanceRecordDao {
    @Query("SELECT * FROM maintenance_records WHERE vehicleId = :vehicleId ORDER BY date DESC")
    fun getByVehicleId(vehicleId: Long): Flow<List<MaintenanceRecordEntity>>

    @Insert
    suspend fun insert(entity: MaintenanceRecordEntity): Long

    @Delete
    suspend fun delete(entity: MaintenanceRecordEntity)
}
```

#### CostRecordDao

```kotlin
@Dao
interface CostRecordDao {
    @Query("SELECT * FROM cost_records WHERE vehicleId = :vehicleId ORDER BY date DESC")
    fun getByVehicleId(vehicleId: Long): Flow<List<CostRecordEntity>>

    @Query("""
        SELECT * FROM cost_records
        WHERE vehicleId = :vehicleId
          AND date BETWEEN :start AND :end
        ORDER BY date DESC
    """)
    fun getByVehicleIdAndDateRange(
        vehicleId: Long, start: LocalDate, end: LocalDate,
    ): Flow<List<CostRecordEntity>>

    @Insert
    suspend fun insert(entity: CostRecordEntity): Long

    @Delete
    suspend fun delete(entity: CostRecordEntity)
}
```

### 1.4 Database

```kotlin
@Database(
    entities = [
        VehicleEntity::class,
        FuelRecordEntity::class,
        MaintenanceRecordEntity::class,
        CostRecordEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun vehicleDao(): VehicleDao
    abstract fun fuelRecordDao(): FuelRecordDao
    abstract fun maintenanceRecordDao(): MaintenanceRecordDao
    abstract fun costRecordDao(): CostRecordDao

    companion object {
        fun create(context: Context): AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, "stylish_mycars.db")
                .build()
    }
}
```

### 1.5 Mapper

各Entity ↔ DomainModelの変換。Repository内で使用。

```kotlin
// VehicleMapper.kt
fun VehicleEntity.toDomain(): Vehicle = Vehicle(
    id = id, maker = maker, name = name, grade = grade,
    year = year, modelCode = modelCode, plateNumber = plateNumber,
    vin = vin, displacement = displacement, weight = weight,
    color = color, firstRegistrationDate = firstRegistrationDate,
    inspectionExpiry = inspectionExpiry, jibaiExpiry = jibaiExpiry,
    insuranceExpiry = insuranceExpiry, insuranceCompany = insuranceCompany,
    insuranceRank = insuranceRank, taxPaid = taxPaid,
    photoUri = photoUri, memo = memo,
)

fun Vehicle.toEntity(): VehicleEntity = VehicleEntity(
    id = id, maker = maker, name = name, grade = grade,
    year = year, modelCode = modelCode, plateNumber = plateNumber,
    vin = vin, displacement = displacement, weight = weight,
    color = color, firstRegistrationDate = firstRegistrationDate,
    inspectionExpiry = inspectionExpiry, jibaiExpiry = jibaiExpiry,
    insuranceExpiry = insuranceExpiry, insuranceCompany = insuranceCompany,
    insuranceRank = insuranceRank, taxPaid = taxPaid,
    photoUri = photoUri, memo = memo,
    updatedAt = LocalDateTime.now(),
)
```

FuelRecord, MaintenanceRecord, CostRecordも同様のパターン。

### 1.6 Repository実装

```kotlin
// VehicleRepositoryImpl.kt
class VehicleRepositoryImpl(
    private val dao: VehicleDao,
) : VehicleRepository {
    override fun getAll(): Flow<List<Vehicle>> =
        dao.getAll().map { entities -> entities.map { it.toDomain() } }

    override fun getById(id: Long): Flow<Vehicle?> =
        dao.getById(id).map { it?.toDomain() }

    override suspend fun insert(vehicle: Vehicle): Long =
        dao.insert(vehicle.toEntity())

    override suspend fun update(vehicle: Vehicle) =
        dao.update(vehicle.toEntity())

    override suspend fun delete(vehicle: Vehicle) =
        dao.delete(vehicle.toEntity())
}
```

---

## 2. Domain層

### 2.1 Repositoryインターフェース

```kotlin
interface VehicleRepository {
    fun getAll(): Flow<List<Vehicle>>
    fun getById(id: Long): Flow<Vehicle?>
    suspend fun insert(vehicle: Vehicle): Long
    suspend fun update(vehicle: Vehicle)
    suspend fun delete(vehicle: Vehicle)
}

interface FuelRecordRepository {
    fun getByVehicleId(vehicleId: Long): Flow<List<FuelRecord>>
    suspend fun getLatest(vehicleId: Long): FuelRecord?
    suspend fun insert(record: FuelRecord): Long
    suspend fun delete(record: FuelRecord)
}

interface MaintenanceRecordRepository {
    fun getByVehicleId(vehicleId: Long): Flow<List<MaintenanceRecord>>
    suspend fun insert(record: MaintenanceRecord): Long
    suspend fun delete(record: MaintenanceRecord)
}

interface CostRecordRepository {
    fun getByVehicleId(vehicleId: Long): Flow<List<CostRecord>>
    fun getByVehicleIdAndDateRange(
        vehicleId: Long, start: LocalDate, end: LocalDate,
    ): Flow<List<CostRecord>>
    suspend fun insert(record: CostRecord): Long
    suspend fun delete(record: CostRecord)
}
```

### 2.2 UseCase

```kotlin
// 車両
class GetVehiclesUseCase(private val repo: VehicleRepository) {
    operator fun invoke(): Flow<List<Vehicle>> = repo.getAll()
}

class GetVehicleUseCase(private val repo: VehicleRepository) {
    operator fun invoke(id: Long): Flow<Vehicle?> = repo.getById(id)
}

class InsertVehicleUseCase(private val repo: VehicleRepository) {
    suspend operator fun invoke(vehicle: Vehicle): Long = repo.insert(vehicle)
}

class UpdateVehicleUseCase(private val repo: VehicleRepository) {
    suspend operator fun invoke(vehicle: Vehicle) = repo.update(vehicle)
}

class DeleteVehicleUseCase(private val repo: VehicleRepository) {
    suspend operator fun invoke(vehicle: Vehicle) = repo.delete(vehicle)
}

// 給油
class GetFuelRecordsUseCase(private val repo: FuelRecordRepository) {
    operator fun invoke(vehicleId: Long): Flow<List<FuelRecord>> =
        repo.getByVehicleId(vehicleId)
}

class InsertFuelRecordUseCase(
    private val fuelRepo: FuelRecordRepository,
    private val costRepo: CostRecordRepository,
) {
    suspend operator fun invoke(record: FuelRecord): Long {
        val id = fuelRepo.insert(record)
        // 給油記録と同時に費用記録も作成
        costRepo.insert(CostRecord(
            vehicleId = record.vehicleId,
            date = record.date,
            category = CostCategory.FUEL,
            title = "給油 ${record.volume}L",
            amount = record.amount,
        ))
        return id
    }
}

// 整備
class GetMaintenanceRecordsUseCase(private val repo: MaintenanceRecordRepository) {
    operator fun invoke(vehicleId: Long): Flow<List<MaintenanceRecord>> =
        repo.getByVehicleId(vehicleId)
}

class InsertMaintenanceRecordUseCase(
    private val maintenanceRepo: MaintenanceRecordRepository,
    private val costRepo: CostRecordRepository,
) {
    suspend operator fun invoke(record: MaintenanceRecord): Long {
        val id = maintenanceRepo.insert(record)
        if (record.cost > 0) {
            costRepo.insert(CostRecord(
                vehicleId = record.vehicleId,
                date = record.date,
                category = CostCategory.MAINTENANCE,
                title = record.title,
                amount = record.cost,
            ))
        }
        return id
    }
}

// 費用
class GetCostRecordsUseCase(private val repo: CostRecordRepository) {
    operator fun invoke(vehicleId: Long): Flow<List<CostRecord>> =
        repo.getByVehicleId(vehicleId)
}
```

### 2.3 車検満了日の計算（Domain Service）

```kotlin
object InspectionCalculator {
    fun calculateExpiry(firstRegistrationDate: LocalDate): LocalDate {
        // 初回: 初度登録 + 3年
        return firstRegistrationDate.plusYears(3)
    }

    fun calculateNextExpiry(currentExpiry: LocalDate): LocalDate {
        // 2回目以降: 現在の満了日 + 2年
        return currentExpiry.plusYears(2)
    }

    fun daysUntilExpiry(expiry: LocalDate, today: LocalDate = LocalDate.now()): Long =
        ChronoUnit.DAYS.between(today, expiry)
}
```

### 2.4 燃費計算（Domain Service）

```kotlin
object FuelEconomyCalculator {
    fun calculate(
        currentOdometer: Int,
        previousOdometer: Int,
        volume: Double,
    ): Double? {
        if (volume <= 0) return null
        val distance = currentOdometer - previousOdometer
        if (distance <= 0) return null
        return distance / volume
    }
}
```

### 2.5 自動車税計算（Domain Service）

```kotlin
object VehicleTaxCalculator {
    fun calculateTax(displacement: Int?): Int? {
        if (displacement == null) return null
        return when {
            displacement <= 660 -> 10_800   // 軽自動車
            displacement <= 1000 -> 25_000
            displacement <= 1500 -> 30_500
            displacement <= 2000 -> 36_000
            displacement <= 2500 -> 43_500
            displacement <= 3000 -> 50_000
            displacement <= 3500 -> 57_000
            displacement <= 4000 -> 65_500
            displacement <= 4500 -> 75_500
            displacement <= 6000 -> 87_000
            else -> 110_000
        }
    }
}
```

---

## 3. Presentation層

### 3.1 VehicleListScreen

#### UiState

```kotlin
data class VehicleListUiState(
    val vehicles: List<Vehicle> = emptyList(),
    val isLoading: Boolean = true,
)
```

#### Intent

```kotlin
sealed interface VehicleListIntent {
    data object AddVehicle : VehicleListIntent
    data class SelectVehicle(val vehicleId: Long) : VehicleListIntent
}
```

#### ViewModel

```kotlin
class VehicleListViewModel(
    private val getVehiclesUseCase: GetVehiclesUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(VehicleListUiState())
    val uiState: StateFlow<VehicleListUiState> = _uiState.asStateFlow()

    private val _effects = Channel<VehicleListEffect>(Channel.BUFFERED)
    val effects: Flow<VehicleListEffect> = _effects.receiveAsFlow()

    init {
        viewModelScope.launch {
            getVehiclesUseCase().collect { vehicles ->
                _uiState.update { it.copy(vehicles = vehicles, isLoading = false) }
            }
        }
    }

    fun accept(intent: VehicleListIntent) {
        when (intent) {
            is VehicleListIntent.AddVehicle ->
                _effects.trySend(VehicleListEffect.NavigateToEdit(null))
            is VehicleListIntent.SelectVehicle ->
                _effects.trySend(VehicleListEffect.NavigateToDetail(intent.vehicleId))
        }
    }
}

sealed interface VehicleListEffect {
    data class NavigateToDetail(val vehicleId: Long) : VehicleListEffect
    data class NavigateToEdit(val vehicleId: Long?) : VehicleListEffect
}
```

### 3.2 VehicleDetailScreen

#### UiState

```kotlin
data class VehicleDetailUiState(
    val vehicle: Vehicle? = null,
    val recentFuelRecords: List<FuelRecord> = emptyList(),
    val recentMaintenanceRecords: List<MaintenanceRecord> = emptyList(),
    val monthlyCost: Int = 0,
    val isLoading: Boolean = true,
)
```

#### Intent

```kotlin
sealed interface VehicleDetailIntent {
    data object NavigateBack : VehicleDetailIntent
    data object EditVehicle : VehicleDetailIntent
    data object OpenFuelRecords : VehicleDetailIntent
    data object OpenMaintenanceRecords : VehicleDetailIntent
    data object OpenCostList : VehicleDetailIntent
}
```

### 3.3 VehicleEditScreen

#### UiState

```kotlin
data class VehicleEditUiState(
    val vehicleId: Long? = null,
    val maker: String = "",
    val name: String = "",
    val grade: String = "",
    val year: String = "",
    val modelCode: String = "",
    val plateNumber: String = "",
    val displacement: String = "",
    val weight: String = "",
    val color: String = "",
    val firstRegistrationDate: LocalDate? = null,
    val memo: String = "",
    val isEditing: Boolean = false,
    val isSaving: Boolean = false,
)
```

#### Intent

```kotlin
sealed interface VehicleEditIntent {
    data class MakerChanged(val value: String) : VehicleEditIntent
    data class NameChanged(val value: String) : VehicleEditIntent
    data class GradeChanged(val value: String) : VehicleEditIntent
    data class YearChanged(val value: String) : VehicleEditIntent
    data class ModelCodeChanged(val value: String) : VehicleEditIntent
    data class PlateNumberChanged(val value: String) : VehicleEditIntent
    data class DisplacementChanged(val value: String) : VehicleEditIntent
    data class WeightChanged(val value: String) : VehicleEditIntent
    data class ColorChanged(val value: String) : VehicleEditIntent
    data class FirstRegistrationDateChanged(val value: LocalDate?) : VehicleEditIntent
    data class MemoChanged(val value: String) : VehicleEditIntent
    data object Save : VehicleEditIntent
    data object Delete : VehicleEditIntent
    data object NavigateBack : VehicleEditIntent
}
```

### 3.4 FuelRecordScreen

#### UiState

```kotlin
data class FuelRecordUiState(
    val records: List<FuelRecord> = emptyList(),
    val latestRecord: FuelRecord? = null,
    val isDialogOpen: Boolean = false,
    // ダイアログ入力状態
    val inputDate: LocalDate = LocalDate.now(),
    val inputOdometer: String = "",
    val inputVolume: String = "",
    val inputAmount: String = "",
    val inputIsFullTank: Boolean = true,
    val inputMemo: String = "",
)
```

#### Intent

```kotlin
sealed interface FuelRecordIntent {
    data object OpenAddDialog : FuelRecordIntent
    data object CloseDialog : FuelRecordIntent
    data class DateChanged(val value: LocalDate) : FuelRecordIntent
    data class OdometerChanged(val value: String) : FuelRecordIntent
    data class VolumeChanged(val value: String) : FuelRecordIntent
    data class AmountChanged(val value: String) : FuelRecordIntent
    data class FullTankChanged(val value: Boolean) : FuelRecordIntent
    data class MemoChanged(val value: String) : FuelRecordIntent
    data object Save : FuelRecordIntent
    data class DeleteRecord(val record: FuelRecord) : FuelRecordIntent
    data object NavigateBack : FuelRecordIntent
}
```

### 3.5 MaintenanceRecordScreen

#### UiState

```kotlin
data class MaintenanceRecordUiState(
    val records: List<MaintenanceRecord> = emptyList(),
    val isDialogOpen: Boolean = false,
    val inputDate: LocalDate = LocalDate.now(),
    val inputOdometer: String = "",
    val inputCategory: MaintenanceCategory = MaintenanceCategory.OTHER,
    val inputTitle: String = "",
    val inputCost: String = "",
    val inputShopName: String = "",
    val inputMemo: String = "",
)
```

### 3.6 Navigation Destinations

```kotlin
internal sealed interface AppDestination : NavKey
internal data object VehicleListDestination : AppDestination
internal data class VehicleDetailDestination(val vehicleId: Long) : AppDestination
internal data class VehicleEditDestination(val vehicleId: Long?) : AppDestination
internal data class FuelRecordDestination(val vehicleId: Long) : AppDestination
internal data class MaintenanceRecordDestination(val vehicleId: Long) : AppDestination
internal data class CostListDestination(val vehicleId: Long) : AppDestination
```

---

## 4. DI構成（AppModule）

```kotlin
val appModule = module {
    // Database
    single { AppDatabase.create(androidContext()) }
    single { get<AppDatabase>().vehicleDao() }
    single { get<AppDatabase>().fuelRecordDao() }
    single { get<AppDatabase>().maintenanceRecordDao() }
    single { get<AppDatabase>().costRecordDao() }

    // Repository
    single<VehicleRepository> { VehicleRepositoryImpl(get()) }
    single<FuelRecordRepository> { FuelRecordRepositoryImpl(get()) }
    single<MaintenanceRecordRepository> { MaintenanceRecordRepositoryImpl(get()) }
    single<CostRecordRepository> { CostRecordRepositoryImpl(get()) }

    // UseCase - Vehicle
    single { GetVehiclesUseCase(get()) }
    single { GetVehicleUseCase(get()) }
    single { InsertVehicleUseCase(get()) }
    single { UpdateVehicleUseCase(get()) }
    single { DeleteVehicleUseCase(get()) }

    // UseCase - Fuel
    single { GetFuelRecordsUseCase(get()) }
    single { InsertFuelRecordUseCase(get(), get()) }

    // UseCase - Maintenance
    single { GetMaintenanceRecordsUseCase(get()) }
    single { InsertMaintenanceRecordUseCase(get(), get()) }

    // UseCase - Cost
    single { GetCostRecordsUseCase(get()) }

    // ViewModel
    viewModel { VehicleListViewModel(get()) }
    viewModel { params -> VehicleDetailViewModel(params.get(), get(), get(), get(), get()) }
    viewModel { params -> VehicleEditViewModel(params.get(), get(), get(), get()) }
    viewModel { params -> FuelRecordViewModel(params.get(), get(), get()) }
    viewModel { params -> MaintenanceRecordViewModel(params.get(), get(), get()) }
    viewModel { params -> CostListViewModel(params.get(), get()) }
}
```
