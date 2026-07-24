package com.segnities007.stylish_myvehicles.presentation.screen.trip

import com.segnities007.stylish_myvehicles.domain.model.TripPurpose
import java.time.LocalDate

sealed interface TripRecordIntent {
    data object OpenManualAdd : TripRecordIntent
    data class Edit(val recordId: Long) : TripRecordIntent
    data object DismissEdit : TripRecordIntent
    data class TitleChanged(val value: String) : TripRecordIntent
    data class PurposeChanged(val value: TripPurpose) : TripRecordIntent
    data class StartOdometerChanged(val value: String) : TripRecordIntent
    data class EndOdometerChanged(val value: String) : TripRecordIntent
    data class DateChanged(val value: LocalDate) : TripRecordIntent
    data class StartTimeChanged(val value: String) : TripRecordIntent
    data class EndTimeChanged(val value: String) : TripRecordIntent
    data class DistanceChanged(val value: String) : TripRecordIntent
    data object Save : TripRecordIntent
    data class RequestDelete(val recordId: Long) : TripRecordIntent
    data object DismissDelete : TripRecordIntent
    data object ConfirmDelete : TripRecordIntent
}
