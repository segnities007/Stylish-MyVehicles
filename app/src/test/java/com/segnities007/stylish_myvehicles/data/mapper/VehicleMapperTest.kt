package com.segnities007.stylish_myvehicles.data.mapper

import com.segnities007.stylish_myvehicles.data.local.entity.VehicleEntity
import com.segnities007.stylish_myvehicles.domain.model.VehicleCategory
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class VehicleMapperTest {

    private fun sampleEntity(category: String = "CAR") = VehicleEntity(
        id = 1,
        category = category,
        maker = "トヨタ",
        name = "カローラ",
        grade = "G",
        year = 2023,
        modelCode = "ZRE212",
        plateNumber = "品川 500 あ 1234",
        vin = "ABC123456789",
        displacement = 1800,
        weight = 1350,
        maxLoadKg = 5,
        color = "ホワイト",
        firstRegistrationDate = LocalDate.of(2023, 4, 1),
        inspectionExpiry = LocalDate.of(2026, 4, 1),
        jibaiExpiry = LocalDate.of(2026, 4, 1),
        insuranceExpiry = LocalDate.of(2027, 4, 1),
        insuranceCompany = "東京海上",
        insuranceRank = 20,
        photoUri = "content://photo/1",
        memo = "テスト車両",
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
        assertEquals(entity.category, result.category)
        assertEquals(entity.maker, result.maker)
        assertEquals(entity.name, result.name)
        assertEquals(entity.grade, result.grade)
        assertEquals(entity.year, result.year)
        assertEquals(entity.modelCode, result.modelCode)
        assertEquals(entity.plateNumber, result.plateNumber)
        assertEquals(entity.vin, result.vin)
        assertEquals(entity.displacement, result.displacement)
        assertEquals(entity.weight, result.weight)
        assertEquals(entity.maxLoadKg, result.maxLoadKg)
        assertEquals(entity.color, result.color)
        assertEquals(entity.firstRegistrationDate, result.firstRegistrationDate)
        assertEquals(entity.inspectionExpiry, result.inspectionExpiry)
        assertEquals(entity.jibaiExpiry, result.jibaiExpiry)
        assertEquals(entity.insuranceExpiry, result.insuranceExpiry)
        assertEquals(entity.insuranceCompany, result.insuranceCompany)
        assertEquals(entity.insuranceRank, result.insuranceRank)
        assertEquals(entity.photoUri, result.photoUri)
        assertEquals(entity.memo, result.memo)
    }

    // ── カテゴリのフォールバック ──

    @Test
    fun `不正なカテゴリ名はCARにフォールバックする`() {
        // Arrange（準備）
        val entity = sampleEntity(category = "INVALID_CATEGORY")

        // Act（実行）
        val domain = entity.toDomain()

        // Assert（検証）
        assertEquals(VehicleCategory.CAR, domain.category)
    }

    @Test
    fun `空文字カテゴリはCARにフォールバックする`() {
        // Arrange（準備）
        val entity = sampleEntity(category = "")

        // Act（実行）
        val domain = entity.toDomain()

        // Assert（検証）
        assertEquals(VehicleCategory.CAR, domain.category)
    }

    // ── 個別フィールドのマッピング ──

    @Test
    fun `toDomainで全フィールドが正しくマッピングされる`() {
        // Arrange（準備）
        val entity = sampleEntity()

        // Act（実行）
        val domain = entity.toDomain()

        // Assert（検証）
        assertEquals(1L, domain.id)
        assertEquals(VehicleCategory.CAR, domain.category)
        assertEquals("トヨタ", domain.maker)
        assertEquals("カローラ", domain.name)
        assertEquals("G", domain.grade)
        assertEquals(2023, domain.year)
        assertEquals("ZRE212", domain.modelCode)
        assertEquals("品川 500 あ 1234", domain.plateNumber)
        assertEquals("ABC123456789", domain.vin)
        assertEquals(1800, domain.displacement)
        assertEquals(1350, domain.weight)
        assertEquals(5, domain.maxLoadKg)
        assertEquals("ホワイト", domain.color)
        assertEquals(LocalDate.of(2023, 4, 1), domain.firstRegistrationDate)
        assertEquals(LocalDate.of(2026, 4, 1), domain.inspectionExpiry)
        assertEquals(LocalDate.of(2026, 4, 1), domain.jibaiExpiry)
        assertEquals(LocalDate.of(2027, 4, 1), domain.insuranceExpiry)
        assertEquals("東京海上", domain.insuranceCompany)
        assertEquals(20, domain.insuranceRank)
        assertEquals("content://photo/1", domain.photoUri)
        assertEquals("テスト車両", domain.memo)
    }

    @Test
    fun `toEntityでカテゴリがenum名文字列として保存される`() {
        // Arrange（準備）
        val entity = sampleEntity(category = "MOTORCYCLE")

        // Act（実行）
        val domain = entity.toDomain()
        val result = domain.toEntity()

        // Assert（検証）
        assertEquals("MOTORCYCLE", result.category)
    }

    @Test
    fun `nullableフィールドがnullのままマッピングされる`() {
        // Arrange（準備）
        val entity = VehicleEntity(
            id = 2,
            maker = "ホンダ",
            name = "CBR250R",
            year = null,
            displacement = null,
            weight = null,
            maxLoadKg = null,
            firstRegistrationDate = null,
            inspectionExpiry = null,
            jibaiExpiry = null,
            insuranceExpiry = null,
            insuranceRank = null,
            photoUri = null,
        )

        // Act（実行）
        val domain = entity.toDomain()

        // Assert（検証）
        assertEquals(null, domain.year)
        assertEquals(null, domain.displacement)
        assertEquals(null, domain.weight)
        assertEquals(null, domain.maxLoadKg)
        assertEquals(null, domain.firstRegistrationDate)
        assertEquals(null, domain.inspectionExpiry)
        assertEquals(null, domain.jibaiExpiry)
        assertEquals(null, domain.insuranceExpiry)
        assertEquals(null, domain.insuranceRank)
        assertEquals(null, domain.photoUri)
    }
}
