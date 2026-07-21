package com.segnities007.stylish_myvehicles.domain.service

import com.segnities007.stylish_myvehicles.domain.model.VehicleCategory

object VehicleTaxCalculator {

    /** 後方互換: 乗用車の自動車税（種別割・排気量基準）。 */
    fun calculateTax(displacement: Int?): Int? = calculateCarTax(displacement)

    /** カテゴリに応じて税額を計算する。該当しない場合は null。 */
    fun calculateTax(
        category: VehicleCategory,
        displacement: Int?,
        maxLoadKg: Int? = null,
    ): Int? = when (category) {
        VehicleCategory.CAR -> calculateCarTax(displacement)
        VehicleCategory.KEI_CAR -> 10_800
        VehicleCategory.MOTORCYCLE -> calculateMotorcycleTax(displacement)
        VehicleCategory.TRUCK -> calculateTruckTax(maxLoadKg)
        VehicleCategory.BICYCLE -> null
        VehicleCategory.OTHER -> null
    }

    /** 乗用車（自動車税 種別割・排気量基準）。 */
    fun calculateCarTax(displacement: Int?): Int? {
        if (displacement == null) return null
        return when {
            displacement <= 660 -> 10_800
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

    /** バイク（軽自動車税 種別割・排気量区分）。 */
    fun calculateMotorcycleTax(displacement: Int?): Int? {
        if (displacement == null) return null
        return when {
            displacement <= 50 -> 2_000
            displacement <= 90 -> 2_000
            displacement <= 125 -> 2_400
            displacement <= 250 -> 3_600
            else -> 6_000
        }
    }

    /** トラック（自動車税 種別割・最大積載量基準）。 */
    fun calculateTruckTax(maxLoadKg: Int?): Int? {
        if (maxLoadKg == null) return null
        return when {
            maxLoadKg <= 1000 -> 8_000
            maxLoadKg <= 2000 -> 11_500
            maxLoadKg <= 3000 -> 16_000
            maxLoadKg <= 4000 -> 20_500
            maxLoadKg <= 5000 -> 25_500
            maxLoadKg <= 6000 -> 30_500
            maxLoadKg <= 7000 -> 35_500
            maxLoadKg <= 8000 -> 40_500
            else -> 45_500
        }
    }
}
