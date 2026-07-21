package com.segnities007.stylish_mycars.domain.model

import com.segnities007.stylish_mycars.domain.service.InspectionCalculator
import java.time.LocalDate

data class Vehicle(
    val id: Long = 0,
    val category: VehicleCategory = VehicleCategory.CAR,
    val maker: String,
    val name: String,
    val grade: String = "",
    val year: Int? = null,
    val modelCode: String = "",
    val plateNumber: String = "",
    val vin: String = "",
    val displacement: Int? = null,
    val weight: Int? = null,
    val maxLoadKg: Int? = null,
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
) {
    /**
     * 現在日付基準の車検満了日。カテゴリと初度登録日から導出する。
     * 車検対象外（自転車・250cc以下バイク等）は null。
     */
    val currentInspectionExpiry: LocalDate?
        get() = firstRegistrationDate?.let {
            InspectionCalculator.calculateCurrentExpiry(category, it, displacement)
        } ?: inspectionExpiry
}
