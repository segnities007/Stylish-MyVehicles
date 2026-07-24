package com.segnities007.stylish_myvehicles.presentation.screen.trip

import androidx.compose.runtime.Immutable
import com.segnities007.stylish_myvehicles.domain.model.TripPurpose
import com.segnities007.stylish_myvehicles.domain.model.TripRecord
import java.time.LocalDate
import java.time.LocalTime

@Immutable
data class TripRecordUiState(
    val records: List<TripRecord> = emptyList(),
    val editingRecordId: Long? = null,
    val deletingRecordId: Long? = null,
    val isInputDialogOpen: Boolean = false,
    val inputTitle: String = "",
    val inputPurpose: TripPurpose = TripPurpose.DRIVE,
    val inputStartOdometer: String = "",
    val inputEndOdometer: String = "",
    val inputDate: LocalDate = LocalDate.now(),
    val inputStartTime: String = "",
    val inputEndTime: String = "",
    val inputDistanceKm: String = "",
) {
    val activeRecord: TripRecord? get() = records.firstOrNull { it.isRecording }
    val editingRecord: TripRecord?
        get() = records.firstOrNull { it.id == editingRecordId }
    val canSave: Boolean
        get() = runCatching {
            LocalTime.parse(inputStartTime)
            LocalTime.parse(inputEndTime)
            inputDistanceKm.toDouble()
        }.isSuccess
}
