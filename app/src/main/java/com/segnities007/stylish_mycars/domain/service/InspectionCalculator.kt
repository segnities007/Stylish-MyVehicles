package com.segnities007.stylish_mycars.domain.service

import com.segnities007.stylish_mycars.domain.model.VehicleCategory
import java.time.LocalDate
import java.time.temporal.ChronoUnit

object InspectionCalculator {

    // ── 既存（乗用車前提・後方互換） ──

    fun calculateFirstExpiry(firstRegistrationDate: LocalDate): LocalDate =
        firstRegistrationDate.plusYears(3)

    fun calculateNextExpiry(currentExpiry: LocalDate): LocalDate =
        currentExpiry.plusYears(2)

    /**
     * 初度登録日から、[today] 以降に最初に到来する車検満了日（乗用車前提）を計算する。
     * 初回3年、以降2年ごと。
     */
    fun calculateCurrentExpiry(
        firstRegistrationDate: LocalDate,
        today: LocalDate = LocalDate.now(),
    ): LocalDate = calculateCurrentExpiry(
        category = VehicleCategory.CAR,
        firstRegistrationDate = firstRegistrationDate,
        displacement = null,
        today = today,
    ) ?: firstRegistrationDate.plusYears(3)

    fun daysUntilExpiry(expiry: LocalDate, today: LocalDate = LocalDate.now()): Long =
        ChronoUnit.DAYS.between(today, expiry)

    // ── カテゴリ対応 ──

    /** その乗り物が車検対象かどうか。バイクは250cc超のみ対象。 */
    fun requiresInspection(category: VehicleCategory, displacement: Int?): Boolean =
        when (category) {
            VehicleCategory.CAR, VehicleCategory.KEI_CAR, VehicleCategory.TRUCK -> true
            VehicleCategory.MOTORCYCLE -> displacement != null && displacement > 250
            VehicleCategory.BICYCLE, VehicleCategory.OTHER -> false
        }

    /** 初回車検までの年数（乗用車・バイク3年、トラック2年）。 */
    fun firstInspectionYears(category: VehicleCategory): Int = when (category) {
        VehicleCategory.TRUCK -> 2
        else -> 3
    }

    /** 継続車検の間隔年数（乗用車・バイク2年、トラック1年）。 */
    fun inspectionIntervalYears(category: VehicleCategory): Int = when (category) {
        VehicleCategory.TRUCK -> 1
        else -> 2
    }

    /**
     * カテゴリ・初度登録日から、[today] 以降に最初に到来する車検満了日を計算する。
     * 車検対象外（自転車・250cc以下バイク等）は null。
     */
    fun calculateCurrentExpiry(
        category: VehicleCategory,
        firstRegistrationDate: LocalDate,
        displacement: Int?,
        today: LocalDate = LocalDate.now(),
    ): LocalDate? {
        if (!requiresInspection(category, displacement)) return null
        var expiry = firstRegistrationDate.plusYears(firstInspectionYears(category).toLong())
        val interval = inspectionIntervalYears(category).toLong()
        while (expiry <= today) {
            expiry = expiry.plusYears(interval)
        }
        return expiry
    }
}
