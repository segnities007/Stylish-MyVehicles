package com.segnities007.stylish_myvehicles.domain.usecase

import com.segnities007.stylish_myvehicles.domain.model.CostCategory
import com.segnities007.stylish_myvehicles.domain.model.CostRecord
import com.segnities007.stylish_myvehicles.domain.model.FuelRecord
import com.segnities007.stylish_myvehicles.domain.model.MaintenanceCategory
import com.segnities007.stylish_myvehicles.domain.model.MaintenanceRecord
import com.segnities007.stylish_myvehicles.domain.model.Vehicle
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExportDataUseCaseTest {

    // ── 車両CSV ──

    @Test
    fun `車両CSVのヘッダーと行が正しい`() {
        // Arrange（準備）
        val vehicles = listOf(
            Vehicle(
                id = 1,
                maker = "トヨタ",
                name = "カローラ",
                grade = "G",
                year = 2023,
                modelCode = "ZRE212",
                plateNumber = "品川500あ1234",
                displacement = 1800,
                weight = 1350,
                color = "ホワイト",
                firstRegistrationDate = LocalDate.of(2023, 4, 1),
                inspectionExpiry = LocalDate.of(2026, 4, 1),
                jibaiExpiry = LocalDate.of(2026, 4, 1),
                insuranceExpiry = LocalDate.of(2027, 4, 1),
                insuranceCompany = "東京海上",
                insuranceRank = 20,
            ),
        )

        // Act（実行）
        val doc = ExportDataUseCase.exportVehiclesCsv(vehicles)

        // Assert（検証）
        assertEquals("text/csv", doc.mimeType)
        assertTrue(doc.fileName.startsWith("vehicles_"))
        assertTrue(doc.fileName.endsWith(".csv"))

        val lines = doc.content.split("\n")
        assertEquals(2, lines.size)
        assertEquals(
            "ID,メーカー,車種,グレード,年式,型式,ナンバー,排気量(cc),重量(kg),カラー,初度登録日,車検満了日,自賠責満了日,任意保険満了日,保険会社,等級",
            lines[0],
        )
        assertTrue(lines[1].startsWith("1,トヨタ,カローラ,G,2023,ZRE212"))
        assertTrue(lines[1].contains("東京海上"))
        assertTrue(lines[1].contains("20"))
    }

    // ── CSVエスケープ ──

    @Test
    fun `カンマを含むフィールドがダブルクォートでエスケープされる`() {
        // Arrange（準備）
        val vehicles = listOf(
            Vehicle(id = 1, maker = "トヨタ,ホンダ", name = "テスト車"),
        )

        // Act（実行）
        val doc = ExportDataUseCase.exportVehiclesCsv(vehicles)

        // Assert（検証）
        val dataLine = doc.content.split("\n")[1]
        assertTrue(dataLine.contains("\"トヨタ,ホンダ\""))
    }

    @Test
    fun `ダブルクォートを含むフィールドが二重クォートでエスケープされる`() {
        // Arrange（準備）
        val vehicles = listOf(
            Vehicle(id = 1, maker = "トヨタ\"特別\"仕様", name = "テスト車"),
        )

        // Act（実行）
        val doc = ExportDataUseCase.exportVehiclesCsv(vehicles)

        // Assert（検証）
        val dataLine = doc.content.split("\n")[1]
        assertTrue(dataLine.contains("\"トヨタ\"\"特別\"\"仕様\""))
    }

    @Test
    fun `改行を含むフィールドがクォートされる`() {
        // Arrange（準備）
        val vehicles = listOf(
            Vehicle(id = 1, maker = "トヨタ\nホンダ", name = "テスト車"),
        )

        // Act（実行）
        val doc = ExportDataUseCase.exportVehiclesCsv(vehicles)

        // Assert（検証）
        assertTrue(doc.content.contains("\"トヨタ\nホンダ\""))
    }

    // ── 給油CSV ──

    @Test
    fun `給油CSVに燃費が含まれる`() {
        // Arrange（準備）
        val records = listOf(
            FuelRecord(
                id = 1,
                vehicleId = 1,
                date = LocalDate.of(2026, 7, 15),
                odometer = 45230,
                volume = 32.5,
                amount = 5688,
                unitPrice = 175,
                fuelEconomy = 14.2,
                isFullTank = true,
            ),
        )

        // Act（実行）
        val doc = ExportDataUseCase.exportFuelRecordsCsv(records)

        // Assert（検証）
        val lines = doc.content.split("\n")
        assertEquals(
            "ID,車両ID,日付,走行距離(km),給油量(L),金額(円),単価(円/L),燃費(km/L),満タン",
            lines[0],
        )
        assertTrue(lines[1].contains("14.2"))
        assertTrue(lines[1].contains("はい"))
    }

    @Test
    fun `給油CSVで燃費がnullの場合空文字になる`() {
        // Arrange（準備）
        val records = listOf(
            FuelRecord(
                id = 2,
                vehicleId = 1,
                date = LocalDate.of(2026, 7, 1),
                odometer = 44000,
                volume = 28.0,
                amount = 4900,
                fuelEconomy = null,
                isFullTank = false,
            ),
        )

        // Act（実行）
        val doc = ExportDataUseCase.exportFuelRecordsCsv(records)

        // Assert（検証）
        val dataLine = doc.content.split("\n")[1]
        assertTrue(dataLine.contains("いいえ"))
        // 燃費と単価が空 → カンマが連続する
        assertTrue(dataLine.contains(",,"))
    }

    // ── 整備CSV ──

    @Test
    fun `整備CSVのフォーマットが正しい`() {
        // Arrange（準備）
        val records = listOf(
            MaintenanceRecord(
                id = 1,
                vehicleId = 1,
                date = LocalDate.of(2026, 6, 10),
                odometer = 44500,
                category = MaintenanceCategory.OIL,
                title = "エンジンオイル交換",
                cost = 5500,
                shopName = "オートバックス",
            ),
        )

        // Act（実行）
        val doc = ExportDataUseCase.exportMaintenanceRecordsCsv(records)

        // Assert（検証）
        val lines = doc.content.split("\n")
        assertEquals("ID,車両ID,日付,走行距離(km),カテゴリ,内容,費用(円),整備工場", lines[0])
        assertTrue(lines[1].contains("エンジンオイル"))
        assertTrue(lines[1].contains("5500"))
        assertTrue(lines[1].contains("オートバックス"))
    }

    // ── 費用CSV ──

    @Test
    fun `費用CSVのフォーマットが正しい`() {
        // Arrange（準備）
        val records = listOf(
            CostRecord(
                id = 1,
                vehicleId = 1,
                date = LocalDate.of(2026, 7, 1),
                category = CostCategory.INSURANCE,
                title = "任意保険料",
                amount = 45000,
            ),
        )

        // Act（実行）
        val doc = ExportDataUseCase.exportCostRecordsCsv(records)

        // Assert（検証）
        val lines = doc.content.split("\n")
        assertEquals("ID,車両ID,日付,カテゴリ,内容,金額(円)", lines[0])
        assertTrue(lines[1].contains("保険"))
        assertTrue(lines[1].contains("任意保険料"))
        assertTrue(lines[1].contains("45000"))
    }

    // ── 空リスト ──

    @Test
    fun `空リストでもヘッダーのみのCSVが生成される`() {
        // Arrange（準備）
        // Act（実行）
        val doc = ExportDataUseCase.exportVehiclesCsv(emptyList())

        // Assert（検証）
        val lines = doc.content.split("\n")
        assertEquals(1, lines.size)
        assertTrue(lines[0].startsWith("ID,"))
    }
}
