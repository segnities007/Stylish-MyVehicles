package com.segnities007.stylish_myvehicles.domain.service

import com.segnities007.stylish_myvehicles.domain.model.Vehicle
import java.time.LocalDate
import java.time.temporal.ChronoUnit

/** 解決された期限情報。 */
data class DeadlineInfo(
    val label: String,
    val date: LocalDate,
    val daysRemaining: Long,
) {
    val isExpired: Boolean get() = daysRemaining < 0
    val isUrgent: Boolean get() = daysRemaining <= 30
}

/**
 * 車両の各種期限（車検・自賠責・任意保険）の中から
 * 最も緊急（残り日数が最小）のものを解決する。
 */
object DeadlineResolver {

    fun resolve(vehicle: Vehicle, today: LocalDate = LocalDate.now()): DeadlineInfo? {
        val candidates = buildList {
            InspectionCalculator.currentExpiryFor(vehicle, today)?.let { add("車検" to it) }
            vehicle.jibaiExpiry?.let { add("自賠責保険" to it) }
            vehicle.insuranceExpiry?.let { add("任意保険" to it) }
        }
        return candidates
            .map { (label, date) ->
                DeadlineInfo(label, date, ChronoUnit.DAYS.between(today, date))
            }
            .minByOrNull { it.daysRemaining }
    }
}
