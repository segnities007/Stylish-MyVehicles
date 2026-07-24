package com.segnities007.stylish_myvehicles.data.mapper

import com.segnities007.stylish_myvehicles.data.local.entity.FuelRecordEntity
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class FuelRecordMapperTest {

    private fun sampleEntity() = FuelRecordEntity(
        id = 10,
        vehicleId = 1,
        date = LocalDate.of(2026, 7, 15),
        odometer = 45230,
        volume = 32.5,
        amount = 5688,
        unitPrice = 175,
        fuelEconomy = 14.2,
        isFullTank = true,
        memo = "満タン給油",
    )

    // ── ラウンドトリップ ──

    @Test
    fun `EntityからDomainに変換してEntityに戻すと全フィールドが保持される`() {
        // Arrange（準備）
        val entity = sampleEntity()

        // Act（実行）
        val result = entity.toDomain().toEntity()

        // Assert（検証）
        assertEquals(entity.id, result.id)
        assertEquals(entity.vehicleId, result.vehicleId)
        assertEquals(entity.date, result.date)
        assertEquals(entity.odometer, result.odometer)
        assertEquals(entity.volume, result.volume, 0.001)
        assertEquals(entity.amount, result.amount)
        assertEquals(entity.unitPrice, result.unitPrice)
        assertEquals(entity.fuelEconomy, result.fuelEconomy)
        assertEquals(entity.isFullTank, result.isFullTank)
        assertEquals(entity.memo, result.memo)
    }

    // ── fuelEconomy の保持（直近のバグ修正） ──

    @Test
    fun `fuelEconomyがラウンドトリップで保持される`() {
        // Arrange（準備）
        val entity = sampleEntity()

        // Act（実行）
        val domain = entity.toDomain()
        val result = domain.toEntity()

        // Assert（検証）
        assertEquals(14.2, domain.fuelEconomy!!, 0.001)
        assertEquals(14.2, result.fuelEconomy!!, 0.001)
    }

    @Test
    fun `fuelEconomyがnullの場合もラウンドトリップでnullが保持される`() {
        // Arrange（準備）
        val entity = FuelRecordEntity(
            id = 11,
            vehicleId = 1,
            date = LocalDate.of(2026, 7, 1),
            odometer = 44000,
            volume = 28.0,
            amount = 4900,
            fuelEconomy = null,
        )

        // Act（実行）
        val result = entity.toDomain().toEntity()

        // Assert（検証）
        assertEquals(null, result.fuelEconomy)
    }

    // ── 個別フィールドのマッピング ──

    @Test
    fun `toDomainで全フィールドが正しくマッピングされる`() {
        // Arrange（準備）
        val entity = sampleEntity()

        // Act（実行）
        val domain = entity.toDomain()

        // Assert（検証）
        assertEquals(10L, domain.id)
        assertEquals(1L, domain.vehicleId)
        assertEquals(LocalDate.of(2026, 7, 15), domain.date)
        assertEquals(45230, domain.odometer)
        assertEquals(32.5, domain.volume, 0.001)
        assertEquals(5688, domain.amount)
        assertEquals(175, domain.unitPrice)
        assertEquals(14.2, domain.fuelEconomy!!, 0.001)
        assertEquals(true, domain.isFullTank)
        assertEquals("満タン給油", domain.memo)
    }

    @Test
    fun `unitPriceがnullの場合も正しくマッピングされる`() {
        // Arrange（準備）
        val entity = FuelRecordEntity(
            id = 12,
            vehicleId = 2,
            date = LocalDate.of(2026, 6, 1),
            odometer = 10000,
            volume = 40.0,
            amount = 6000,
            unitPrice = null,
            isFullTank = false,
        )

        // Act（実行）
        val domain = entity.toDomain()

        // Assert（検証）
        assertEquals(null, domain.unitPrice)
        assertEquals(false, domain.isFullTank)
    }
}
