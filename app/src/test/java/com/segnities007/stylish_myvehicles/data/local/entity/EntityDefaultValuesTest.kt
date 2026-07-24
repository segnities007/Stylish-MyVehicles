package com.segnities007.stylish_myvehicles.data.local.entity

import java.time.LocalDate
import java.time.LocalDateTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class EntityDefaultValuesTest {

    @Test
    fun `VehicleEntityのデフォルト値が正しい`() {
        // Arrange（準備）
        // Act（実行）
        val entity = VehicleEntity(maker = "トヨタ", name = "カローラ")

        // Assert（検証）
        assertEquals(0L, entity.id)
        assertEquals("CAR", entity.category)
        assertEquals("トヨタ", entity.maker)
        assertEquals("カローラ", entity.name)
        assertEquals("", entity.grade)
        assertNull(entity.year)
        assertEquals("", entity.modelCode)
        assertEquals("", entity.plateNumber)
        assertEquals("", entity.vin)
        assertNull(entity.displacement)
        assertNull(entity.weight)
        assertNull(entity.maxLoadKg)
        assertEquals("", entity.color)
        assertNull(entity.firstRegistrationDate)
        assertNull(entity.inspectionExpiry)
        assertNull(entity.jibaiExpiry)
        assertNull(entity.insuranceExpiry)
        assertEquals("", entity.insuranceCompany)
        assertNull(entity.insuranceRank)
        assertNull(entity.photoUri)
        assertEquals("", entity.memo)
        assertNotNull(entity.createdAt)
        assertNotNull(entity.updatedAt)
    }

    @Test
    fun `VehicleEntityの全フィールドを設定できる`() {
        // Arrange（準備）
        val now = LocalDateTime.of(2026, 7, 24, 10, 0)

        // Act（実行）
        val entity = VehicleEntity(
            id = 1L,
            category = "KEI_CAR",
            maker = "スズキ",
            name = "ワゴンR",
            grade = "FX",
            year = 2023,
            modelCode = "MH34S",
            plateNumber = "品川580あ1234",
            vin = "MH34S100001",
            displacement = 660,
            weight = 780,
            maxLoadKg = null,
            color = "白",
            firstRegistrationDate = LocalDate.of(2023, 4, 1),
            inspectionExpiry = LocalDate.of(2026, 4, 1),
            jibaiExpiry = LocalDate.of(2026, 4, 1),
            insuranceExpiry = LocalDate.of(2027, 4, 1),
            insuranceCompany = "東京海上",
            insuranceRank = 15,
            photoUri = "content://photo/1",
            memo = "テストメモ",
            createdAt = now,
            updatedAt = now,
        )

        // Assert（検証）
        assertEquals(1L, entity.id)
        assertEquals("KEI_CAR", entity.category)
        assertEquals(660, entity.displacement)
        assertEquals(780, entity.weight)
        assertEquals(15, entity.insuranceRank)
        assertEquals("content://photo/1", entity.photoUri)
        assertEquals("テストメモ", entity.memo)
    }

    @Test
    fun `FuelRecordEntityのデフォルト値が正しい`() {
        // Arrange（準備）
        // Act（実行）
        val entity = FuelRecordEntity(
            vehicleId = 1L,
            date = LocalDate.of(2026, 7, 1),
            odometer = 10000,
            volume = 30.0,
            amount = 5000,
        )

        // Assert（検証）
        assertEquals(0L, entity.id)
        assertEquals(1L, entity.vehicleId)
        assertNull(entity.unitPrice)
        assertNull(entity.fuelEconomy)
        assertTrue(entity.isFullTank)
        assertEquals("", entity.memo)
        assertNotNull(entity.createdAt)
    }

    @Test
    fun `FuelRecordEntityの単価と燃費を設定できる`() {
        // Arrange（準備）
        // Act（実行）
        val entity = FuelRecordEntity(
            vehicleId = 1L,
            date = LocalDate.of(2026, 7, 1),
            odometer = 10000,
            volume = 30.0,
            amount = 5000,
            unitPrice = 166,
            fuelEconomy = 12.5,
            isFullTank = false,
        )

        // Assert（検証）
        assertEquals(166, entity.unitPrice)
        assertEquals(12.5, entity.fuelEconomy!!, 0.01)
        assertFalse(entity.isFullTank)
    }

    @Test
    fun `MaintenanceRecordEntityのデフォルト値が正しい`() {
        // Arrange（準備）
        // Act（実行）
        val entity = MaintenanceRecordEntity(
            vehicleId = 1L,
            date = LocalDate.of(2026, 7, 1),
            category = "OIL",
            title = "オイル交換",
        )

        // Assert（検証）
        assertEquals(0L, entity.id)
        assertNull(entity.odometer)
        assertEquals(0, entity.cost)
        assertEquals("", entity.shopName)
        assertEquals("", entity.memo)
        assertNull(entity.photoUri)
        assertNotNull(entity.createdAt)
    }

    @Test
    fun `MaintenanceRecordEntityの全フィールドを設定できる`() {
        // Arrange（準備）
        // Act（実行）
        val entity = MaintenanceRecordEntity(
            id = 1L,
            vehicleId = 1L,
            date = LocalDate.of(2026, 7, 1),
            odometer = 25000,
            category = "BRAKE",
            title = "ブレーキパッド交換",
            cost = 15000,
            shopName = "オートバックス",
            memo = "フロントのみ",
            photoUri = "content://photo/2",
        )

        // Assert（検証）
        assertEquals(25000, entity.odometer)
        assertEquals(15000, entity.cost)
        assertEquals("オートバックス", entity.shopName)
        assertEquals("フロントのみ", entity.memo)
        assertEquals("content://photo/2", entity.photoUri)
    }

    @Test
    fun `CostRecordEntityのデフォルト値が正しい`() {
        // Arrange（準備）
        // Act（実行）
        val entity = CostRecordEntity(
            vehicleId = 1L,
            date = LocalDate.of(2026, 7, 1),
            category = "FUEL",
            title = "給油",
            amount = 5000,
        )

        // Assert（検証）
        assertEquals(0L, entity.id)
        assertEquals("", entity.memo)
        assertNotNull(entity.createdAt)
    }

    @Test
    fun `CostRecordEntityの全フィールドを設定できる`() {
        // Arrange（準備）
        // Act（実行）
        val entity = CostRecordEntity(
            id = 1L,
            vehicleId = 1L,
            date = LocalDate.of(2026, 7, 1),
            category = "TAX",
            title = "自動車税",
            amount = 39500,
            memo = "年払い",
        )

        // Assert（検証）
        assertEquals(1L, entity.id)
        assertEquals("TAX", entity.category)
        assertEquals(39500, entity.amount)
        assertEquals("年払い", entity.memo)
    }

    @Test
    fun `TripRecordEntityのデフォルト値が正しい`() {
        // Arrange（準備）
        // Act（実行）
        val entity = TripRecordEntity(
            vehicleId = 1L,
            title = "ドライブ",
            purpose = "DRIVE",
            startedAt = LocalDateTime.of(2026, 7, 1, 9, 0),
            endedAt = null,
            distanceMeters = 0,
            startOdometer = null,
            endOdometer = null,
            memo = "",
        )

        // Assert（検証）
        assertEquals(0L, entity.id)
        assertNull(entity.endedAt)
        assertEquals(0L, entity.distanceMeters)
        assertNull(entity.startOdometer)
        assertNull(entity.endOdometer)
    }

    @Test
    fun `TripRecordEntityの全フィールドを設定できる`() {
        // Arrange（準備）
        // Act（実行）
        val entity = TripRecordEntity(
            id = 1L,
            vehicleId = 1L,
            title = "通勤",
            purpose = "COMMUTE",
            startedAt = LocalDateTime.of(2026, 7, 1, 8, 0),
            endedAt = LocalDateTime.of(2026, 7, 1, 9, 0),
            distanceMeters = 15000,
            startOdometer = 10000,
            endOdometer = 10015,
            memo = "テスト",
        )

        // Assert（検証）
        assertEquals(1L, entity.id)
        assertEquals("COMMUTE", entity.purpose)
        assertEquals(15000L, entity.distanceMeters)
        assertEquals(10000, entity.startOdometer)
        assertEquals(10015, entity.endOdometer)
        assertEquals("テスト", entity.memo)
    }

    @Test
    fun `TripLocationPointEntityの全フィールドを設定できる`() {
        // Arrange（準備）
        val now = LocalDateTime.of(2026, 7, 1, 9, 0)

        // Act（実行）
        val entity = TripLocationPointEntity(
            id = 1L,
            tripId = 1L,
            latitude = 35.6762,
            longitude = 139.6503,
            recordedAt = now,
            accuracyMeters = 10.5f,
        )

        // Assert（検証）
        assertEquals(1L, entity.id)
        assertEquals(1L, entity.tripId)
        assertEquals(35.6762, entity.latitude, 0.0001)
        assertEquals(139.6503, entity.longitude, 0.0001)
        assertEquals(now, entity.recordedAt)
        assertEquals(10.5f, entity.accuracyMeters, 0.01f)
    }

    @Test
    fun `MaintenanceScheduleEntityのデフォルト値が正しい`() {
        // Arrange（準備）
        // Act（実行）
        val entity = MaintenanceScheduleEntity(
            vehicleId = 1L,
            category = "OIL",
        )

        // Assert（検証）
        assertEquals(0L, entity.id)
        assertNull(entity.intervalKm)
        assertNull(entity.intervalMonths)
        assertNull(entity.lastDoneDate)
        assertNull(entity.lastDoneOdometer)
    }

    @Test
    fun `MaintenanceScheduleEntityの全フィールドを設定できる`() {
        // Arrange（準備）
        // Act（実行）
        val entity = MaintenanceScheduleEntity(
            id = 1L,
            vehicleId = 1L,
            category = "OIL",
            intervalKm = 5000,
            intervalMonths = 6,
            lastDoneDate = LocalDate.of(2026, 1, 15),
            lastDoneOdometer = 20000,
        )

        // Assert（検証）
        assertEquals(1L, entity.id)
        assertEquals(5000, entity.intervalKm)
        assertEquals(6, entity.intervalMonths)
        assertEquals(LocalDate.of(2026, 1, 15), entity.lastDoneDate)
        assertEquals(20000, entity.lastDoneOdometer)
    }

    @Test
    fun `VehicleEntityのcopyでフィールドを変更できる`() {
        // Arrange（準備）
        val original = VehicleEntity(maker = "トヨタ", name = "カローラ")

        // Act（実行）
        val modified = original.copy(maker = "ホンダ", year = 2025)

        // Assert（検証）
        assertEquals("ホンダ", modified.maker)
        assertEquals("カローラ", modified.name)
        assertEquals(2025, modified.year)
    }

    @Test
    fun `FuelRecordEntityのcopyでフィールドを変更できる`() {
        // Arrange（準備）
        val original = FuelRecordEntity(
            vehicleId = 1L,
            date = LocalDate.of(2026, 7, 1),
            odometer = 10000,
            volume = 30.0,
            amount = 5000,
        )

        // Act（実行）
        val modified = original.copy(odometer = 10500, volume = 35.0)

        // Assert（検証）
        assertEquals(10500, modified.odometer)
        assertEquals(35.0, modified.volume, 0.01)
        assertEquals(5000, modified.amount)
    }
}
