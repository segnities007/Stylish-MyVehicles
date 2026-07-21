package com.segnities007.stylish_mycars.domain.usecase

import com.segnities007.stylish_mycars.domain.model.CostRecord
import com.segnities007.stylish_mycars.domain.model.FuelRecord
import com.segnities007.stylish_mycars.domain.model.MaintenanceRecord
import com.segnities007.stylish_mycars.domain.model.Vehicle

data class ExportDocument(
    val fileName: String,
    val mimeType: String,
    val content: String,
)

class ExportDataUseCase {
    fun exportVehiclesCsv(vehicles: List<Vehicle>): ExportDocument {
        val header = "ID,メーカー,車種,グレード,年式,型式,ナンバー,排気量(cc),重量(kg),カラー,初度登録日,車検満了日,自賠責満了日,任意保険満了日,保険会社,等級,税金納付済み,メモ"
        val rows = vehicles.map { v ->
            listOf(
                v.id, v.maker, v.name, v.grade, v.year ?: "",
                v.modelCode, v.plateNumber, v.displacement ?: "",
                v.weight ?: "", v.color, v.firstRegistrationDate ?: "",
                v.inspectionExpiry ?: "", v.jibaiExpiry ?: "",
                v.insuranceExpiry ?: "", v.insuranceCompany,
                v.insuranceRank ?: "", if (v.taxPaid) "はい" else "いいえ",
                v.memo.replace(",", "、"),
            ).joinToString(",")
        }
        return ExportDocument(
            fileName = "vehicles_${java.time.LocalDate.now()}.csv",
            mimeType = "text/csv",
            content = (listOf(header) + rows).joinToString("\n"),
        )
    }

    fun exportFuelRecordsCsv(records: List<FuelRecord>): ExportDocument {
        val header = "ID,車両ID,日付,走行距離(km),給油量(L),金額(円),単価(円/L),燃費(km/L),満タン,メモ"
        val rows = records.map { r ->
            listOf(
                r.id, r.vehicleId, r.date, r.odometer, r.volume,
                r.amount, r.unitPrice ?: "", r.fuelEconomy ?: "",
                if (r.isFullTank) "はい" else "いいえ",
                r.memo.replace(",", "、"),
            ).joinToString(",")
        }
        return ExportDocument(
            fileName = "fuel_records_${java.time.LocalDate.now()}.csv",
            mimeType = "text/csv",
            content = (listOf(header) + rows).joinToString("\n"),
        )
    }

    fun exportMaintenanceRecordsCsv(records: List<MaintenanceRecord>): ExportDocument {
        val header = "ID,車両ID,日付,走行距離(km),カテゴリ,内容,費用(円),整備工場,メモ"
        val rows = records.map { r ->
            listOf(
                r.id, r.vehicleId, r.date, r.odometer ?: "",
                r.category.label, r.title.replace(",", "、"),
                r.cost, r.shopName.replace(",", "、"),
                r.memo.replace(",", "、"),
            ).joinToString(",")
        }
        return ExportDocument(
            fileName = "maintenance_records_${java.time.LocalDate.now()}.csv",
            mimeType = "text/csv",
            content = (listOf(header) + rows).joinToString("\n"),
        )
    }

    fun exportCostRecordsCsv(records: List<CostRecord>): ExportDocument {
        val header = "ID,車両ID,日付,カテゴリ,内容,金額(円),メモ"
        val rows = records.map { r ->
            listOf(
                r.id, r.vehicleId, r.date, r.category.label,
                r.title.replace(",", "、"), r.amount,
                r.memo.replace(",", "、"),
            ).joinToString(",")
        }
        return ExportDocument(
            fileName = "cost_records_${java.time.LocalDate.now()}.csv",
            mimeType = "text/csv",
            content = (listOf(header) + rows).joinToString("\n"),
        )
    }
}
