package com.segnities007.stylish_myvehicles.data.mapper

import com.segnities007.stylish_myvehicles.data.local.entity.CostRecordEntity
import com.segnities007.stylish_myvehicles.domain.model.CostCategory
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class CostRecordMapperTest {

    private fun sampleEntity() = CostRecordEntity(
        id = 30,
        vehicleId = 1,
        date = LocalDate.of(2026, 7, 1),
        category = "INSURANCE",
        title = "任意保険料",
        amount = 45000,
        memo = "年間一括払い",
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
        assertEquals(entity.category, result.category)
        assertEquals(entity.title, result.title)
        assertEquals(entity.amount, result.amount)
        assertEquals(entity.memo, result.memo)
    }

    // ── 個別フィールドのマッピング ──

    @Test
    fun `toDomainで全フィールドが正しくマッピングされる`() {
        // Arrange（準備）
        val entity = sampleEntity()

        // Act（実行）
        val domain = entity.toDomain()

        // Assert（検証）
        assertEquals(30L, domain.id)
        assertEquals(1L, domain.vehicleId)
        assertEquals(LocalDate.of(2026, 7, 1), domain.date)
        assertEquals(CostCategory.INSURANCE, domain.category)
        assertEquals("任意保険料", domain.title)
        assertEquals(45000, domain.amount)
        assertEquals("年間一括払い", domain.memo)
    }

    @Test
    fun `toEntityでカテゴリがenum名文字列として保存される`() {
        // Arrange（準備）
        val entity = sampleEntity()

        // Act（実行）
        val domain = entity.toDomain()
        val result = domain.toEntity()

        // Assert（検証）
        assertEquals("INSURANCE", result.category)
    }

    @Test
    fun `全カテゴリのラウンドトリップが正しく動作する`() {
        // Arrange（準備）
        val categories = CostCategory.entries

        for (cat in categories) {
            // Act（実行）
            val entity = CostRecordEntity(
                vehicleId = 1,
                date = LocalDate.of(2026, 1, 1),
                category = cat.name,
                title = "テスト",
                amount = 1000,
            )
            val result = entity.toDomain().toEntity()

            // Assert（検証）
            assertEquals(cat.name, result.category)
        }
    }
}
