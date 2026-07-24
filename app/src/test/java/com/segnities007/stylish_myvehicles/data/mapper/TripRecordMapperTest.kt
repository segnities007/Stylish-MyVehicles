package com.segnities007.stylish_myvehicles.data.mapper

import com.segnities007.stylish_myvehicles.data.local.entity.TripRecordEntity
import com.segnities007.stylish_myvehicles.domain.model.TripPurpose
import java.time.LocalDateTime
import org.junit.Assert.assertEquals
import org.junit.Test

class TripRecordMapperTest {

    private fun sampleEntity() = TripRecordEntity(
        id = 40,
        vehicleId = 1,
        title = "箱根ドライブ",
        purpose = "DRIVE",
        startedAt = LocalDateTime.of(2026, 7, 20, 8, 30),
        endedAt = LocalDateTime.of(2026, 7, 20, 17, 45),
        distanceMeters = 125_000,
        startOdometer = 45000,
        endOdometer = 45125,
        memo = "日帰り温泉",
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
        assertEquals(entity.title, result.title)
        assertEquals(entity.purpose, result.purpose)
        assertEquals(entity.startedAt, result.startedAt)
        assertEquals(entity.endedAt, result.endedAt)
        assertEquals(entity.distanceMeters, result.distanceMeters)
        assertEquals(entity.startOdometer, result.startOdometer)
        assertEquals(entity.endOdometer, result.endOdometer)
        assertEquals(entity.memo, result.memo)
    }

    // ── purpose のフォールバック ──

    @Test
    fun `不明なpurposeはOTHERにフォールバックする`() {
        // Arrange（準備）
        val entity = TripRecordEntity(
            id = 41,
            vehicleId = 1,
            title = "テスト",
            purpose = "UNKNOWN_PURPOSE",
            startedAt = LocalDateTime.of(2026, 7, 1, 9, 0),
            endedAt = null,
            distanceMeters = 0,
            startOdometer = null,
            endOdometer = null,
            memo = "",
        )

        // Act（実行）
        val domain = entity.toDomain()

        // Assert（検証）
        assertEquals(TripPurpose.OTHER, domain.purpose)
    }

    @Test
    fun `空文字purposeはOTHERにフォールバックする`() {
        // Arrange（準備）
        val entity = TripRecordEntity(
            id = 42,
            vehicleId = 1,
            title = "テスト",
            purpose = "",
            startedAt = LocalDateTime.of(2026, 7, 1, 9, 0),
            endedAt = null,
            distanceMeters = 0,
            startOdometer = null,
            endOdometer = null,
            memo = "",
        )

        // Act（実行）
        val domain = entity.toDomain()

        // Assert（検証）
        assertEquals(TripPurpose.OTHER, domain.purpose)
    }

    // ── 個別フィールドのマッピング ──

    @Test
    fun `toDomainで全フィールドが正しくマッピングされる`() {
        // Arrange（準備）
        val entity = sampleEntity()

        // Act（実行）
        val domain = entity.toDomain()

        // Assert（検証）
        assertEquals(40L, domain.id)
        assertEquals(1L, domain.vehicleId)
        assertEquals("箱根ドライブ", domain.title)
        assertEquals(TripPurpose.DRIVE, domain.purpose)
        assertEquals(LocalDateTime.of(2026, 7, 20, 8, 30), domain.startedAt)
        assertEquals(LocalDateTime.of(2026, 7, 20, 17, 45), domain.endedAt)
        assertEquals(125_000L, domain.distanceMeters)
        assertEquals(45000, domain.startOdometer)
        assertEquals(45125, domain.endOdometer)
        assertEquals("日帰り温泉", domain.memo)
    }

    @Test
    fun `toEntityでpurposeがenum名文字列として保存される`() {
        // Arrange（準備）
        val entity = sampleEntity()

        // Act（実行）
        val domain = entity.toDomain()
        val result = domain.toEntity()

        // Assert（検証）
        assertEquals("DRIVE", result.purpose)
    }

    @Test
    fun `全purposeのラウンドトリップが正しく動作する`() {
        // Arrange（準備）
        val purposes = TripPurpose.entries

        for (purpose in purposes) {
            // Act（実行）
            val entity = TripRecordEntity(
                vehicleId = 1,
                title = "テスト",
                purpose = purpose.name,
                startedAt = LocalDateTime.of(2026, 1, 1, 0, 0),
                endedAt = null,
                distanceMeters = 0,
                startOdometer = null,
                endOdometer = null,
                memo = "",
            )
            val result = entity.toDomain().toEntity()

            // Assert（検証）
            assertEquals(purpose.name, result.purpose)
        }
    }

    @Test
    fun `endedAtがnullの場合も正しくマッピングされる`() {
        // Arrange（準備）
        val entity = TripRecordEntity(
            id = 43,
            vehicleId = 1,
            title = "記録中",
            purpose = "COMMUTE",
            startedAt = LocalDateTime.of(2026, 7, 24, 8, 0),
            endedAt = null,
            distanceMeters = 0,
            startOdometer = 45200,
            endOdometer = null,
            memo = "",
        )

        // Act（実行）
        val domain = entity.toDomain()

        // Assert（検証）
        assertEquals(null, domain.endedAt)
        assertEquals(null, domain.endOdometer)
        assertEquals(true, domain.isRecording)
    }
}
