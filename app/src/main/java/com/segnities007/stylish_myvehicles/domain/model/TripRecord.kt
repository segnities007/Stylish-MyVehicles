package com.segnities007.stylish_myvehicles.domain.model

import java.time.LocalDateTime

enum class TripPurpose(val label: String) {
    DRIVE("ドライブ"),
    COMMUTE("通勤・通学"),
    SHOPPING("買い物"),
    BUSINESS("仕事"),
    TRAVEL("旅行"),
    OTHER("その他"),
}

data class TripRecord(
    val id: Long = 0,
    val vehicleId: Long,
    val title: String = "",
    val purpose: TripPurpose = TripPurpose.DRIVE,
    val startedAt: LocalDateTime,
    val endedAt: LocalDateTime? = null,
    val distanceMeters: Long = 0,
    val startOdometer: Int? = null,
    val endOdometer: Int? = null,
    val memo: String = "",
) {
    val isRecording: Boolean get() = endedAt == null
}
