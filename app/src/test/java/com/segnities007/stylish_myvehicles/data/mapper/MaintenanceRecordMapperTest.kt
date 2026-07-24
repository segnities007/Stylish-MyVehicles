package com.segnities007.stylish_myvehicles.data.mapper

import com.segnities007.stylish_myvehicles.data.local.entity.MaintenanceRecordEntity
import com.segnities007.stylish_myvehicles.domain.model.MaintenanceCategory
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class MaintenanceRecordMapperTest {

    private fun sampleEntity() = MaintenanceRecordEntity(
        id = 20,
        vehicleId = 1,
        date = LocalDate.of(2026, 6, 10),
        odometer = 44500,
        category = "OIL",
        title = "エンジンオイル交換",
        cost = 5500,
        shopName = "オートバックス",
        memo = "0W-20 使用",
        photoUri = "content://photo/maint/1",
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
        assertEquals(entity.category, result.category)
        assertEquals(entity.title, result.title)
        assertEquals(entity.cost, result.cost)
        assertEquals(entity.shopName, result.shopName)
        assertEquals(entity.memo, result.memo)
        assertEquals(entity.photoUri, result.photoUri)
    }

    // ── 個別フィールドのマッピング ──

    @Test
    fun `toDomainで全フィールドが正しくマッピングされる`() {
        // Arrange（準備）
        val entity = sampleEntity()

        // Act（実行）
        val domain = entity.toDomain()

        // Assert（検証）
        assertEquals(20L, domain.id)
        assertEquals(1L, domain.vehicleId)
        assertEquals(LocalDate.of(2026, 6, 10), domain.date)
        assertEquals(44500, domain.odometer)
        assertEquals(MaintenanceCategory.OIL, domain.category)
        assertEquals("エンジンオイル交換", domain.title)
        assertEquals(5500, domain.cost)
        assertEquals("オートバックス", domain.shopName)
        assertEquals("0W-20 使用", domain.memo)
        assertEquals("content://photo/maint/1", domain.photoUri)
    }

    @Test
    fun `toEntityでカテゴリがenum名文字列として保存される`() {
        // Arrange（準備）
        val entity = sampleEntity()

        // Act（実行）
        val domain = entity.toDomain()
        val result = domain.toEntity()

        // Assert（検証）
        assertEquals("OIL", result.category)
    }

    @Test
    fun `nullableフィールドがnullのままマッピングされる`() {
        // Arrange（準備）
        val entity = MaintenanceRecordEntity(
            id = 21,
            vehicleId = 2,
            date = LocalDate.of(2026, 5, 1),
            odometer = null,
            category = "TIRE",
            title = "タイヤ交換",
            photoUri = null,
        )

        // Act（実行）
        val domain = entity.toDomain()

        // Assert（検証）
        assertEquals(null, domain.odometer)
        assertEquals(null, domain.photoUri)
        assertEquals(MaintenanceCategory.TIRE, domain.category)
    }

    @Test
    fun `全カテゴリのラウンドトリップが正しく動作する`() {
        // Arrange（準備）
        val categories = MaintenanceCategory.entries

        for (cat in categories) {
            // Act（実行）
            val entity = MaintenanceRecordEntity(
                vehicleId = 1,
                date = LocalDate.of(2026, 1, 1),
                category = cat.name,
                title = "テスト",
            )
            val result = entity.toDomain().toEntity()

            // Assert（検証）
            assertEquals(cat.name, result.category)
        }
    }
}
