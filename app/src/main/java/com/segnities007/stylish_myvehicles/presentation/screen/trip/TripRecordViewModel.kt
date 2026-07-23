package com.segnities007.stylish_myvehicles.presentation.screen.trip

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.segnities007.stylish_myvehicles.domain.usecase.trip.DeleteTripRecordUseCase
import com.segnities007.stylish_myvehicles.domain.usecase.trip.GetTripRecordsUseCase
import com.segnities007.stylish_myvehicles.domain.usecase.trip.InsertTripRecordUseCase
import com.segnities007.stylish_myvehicles.domain.usecase.trip.UpdateTripRecordUseCase
import com.segnities007.stylish_myvehicles.presentation.util.normalizeDecimalInput
import com.segnities007.stylish_myvehicles.presentation.util.normalizeIntegerInput
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.math.roundToLong

class TripRecordViewModel(
    private val vehicleId: Long,
    getTripRecordsUseCase: GetTripRecordsUseCase,
    private val insertTripRecordUseCase: InsertTripRecordUseCase,
    private val updateTripRecordUseCase: UpdateTripRecordUseCase,
    private val deleteTripRecordUseCase: DeleteTripRecordUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(TripRecordUiState())
    val uiState: StateFlow<TripRecordUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getTripRecordsUseCase(vehicleId).collect { records ->
                _uiState.update { it.copy(records = records) }
            }
        }
    }

    fun accept(intent: TripRecordIntent) {
        when (intent) {
            TripRecordIntent.OpenManualAdd -> {
                val now = LocalTime.now()
                _uiState.update {
                    it.copy(
                        isInputDialogOpen = true,
                        editingRecordId = null,
                        inputTitle = "",
                        inputPurpose = com.segnities007.stylish_myvehicles.domain.model.TripPurpose.DRIVE,
                        inputStartOdometer = "",
                        inputEndOdometer = "",
                        inputDate = LocalDate.now(),
                        inputStartTime = now.withSecond(0).withNano(0).toString(),
                        inputEndTime = now.plusHours(1).withSecond(0).withNano(0).toString(),
                        inputDistanceKm = "",
                    )
                }
            }
            is TripRecordIntent.Edit -> {
                val record = _uiState.value.records.firstOrNull { it.id == intent.recordId }
                    ?: return
                _uiState.update {
                    it.copy(
                        editingRecordId = record.id,
                        isInputDialogOpen = true,
                        inputTitle = record.title,
                        inputPurpose = record.purpose,
                        inputStartOdometer = record.startOdometer?.toString().orEmpty(),
                        inputEndOdometer = record.endOdometer?.toString().orEmpty(),
                        inputDate = record.startedAt.toLocalDate(),
                        inputStartTime = record.startedAt.toLocalTime()
                            .withSecond(0).withNano(0).toString(),
                        inputEndTime = record.endedAt?.toLocalTime()
                            ?.withSecond(0)?.withNano(0)?.toString().orEmpty(),
                        inputDistanceKm = "%.1f".format(record.distanceMeters / 1000.0),
                    )
                }
            }
            TripRecordIntent.DismissEdit ->
                _uiState.update { it.copy(editingRecordId = null, isInputDialogOpen = false) }
            is TripRecordIntent.TitleChanged ->
                _uiState.update { it.copy(inputTitle = intent.value) }
            is TripRecordIntent.PurposeChanged ->
                _uiState.update { it.copy(inputPurpose = intent.value) }
            is TripRecordIntent.StartOdometerChanged ->
                _uiState.update {
                    it.copy(inputStartOdometer = intent.value.normalizeIntegerInput())
                }
            is TripRecordIntent.EndOdometerChanged ->
                _uiState.update {
                    it.copy(inputEndOdometer = intent.value.normalizeIntegerInput())
                }
            is TripRecordIntent.DateChanged ->
                _uiState.update { it.copy(inputDate = intent.value) }
            is TripRecordIntent.StartTimeChanged ->
                _uiState.update {
                    it.copy(inputStartTime = intent.value.filter { c -> c.isDigit() || c == ':' })
                }
            is TripRecordIntent.EndTimeChanged ->
                _uiState.update {
                    it.copy(inputEndTime = intent.value.filter { c -> c.isDigit() || c == ':' })
                }
            is TripRecordIntent.DistanceChanged ->
                _uiState.update {
                    it.copy(inputDistanceKm = intent.value.normalizeDecimalInput())
                }
            TripRecordIntent.Save -> save()
            is TripRecordIntent.RequestDelete ->
                _uiState.update { it.copy(deletingRecordId = intent.recordId) }
            TripRecordIntent.DismissDelete ->
                _uiState.update { it.copy(deletingRecordId = null) }
            TripRecordIntent.ConfirmDelete -> delete()
        }
    }

    private fun save() {
        val state = _uiState.value
        if (!state.canSave) return
        val startTime = LocalTime.parse(state.inputStartTime)
        val endTime = LocalTime.parse(state.inputEndTime)
        val startedAt = LocalDateTime.of(state.inputDate, startTime)
        var endedAt = LocalDateTime.of(state.inputDate, endTime)
        if (endedAt.isBefore(startedAt)) endedAt = endedAt.plusDays(1)
        val distanceMeters = (state.inputDistanceKm.toDouble() * 1000).roundToLong()
        viewModelScope.launch {
            val values = state.editingRecord?.copy(
                    title = state.inputTitle.trim(),
                    purpose = state.inputPurpose,
                    startOdometer = state.inputStartOdometer.toIntOrNull(),
                    endOdometer = state.inputEndOdometer.toIntOrNull(),
                    startedAt = startedAt,
                    endedAt = endedAt,
                    distanceMeters = distanceMeters,
                ) ?: com.segnities007.stylish_myvehicles.domain.model.TripRecord(
                    vehicleId = vehicleId,
                    title = state.inputTitle.trim(),
                    purpose = state.inputPurpose,
                    startedAt = startedAt,
                    endedAt = endedAt,
                    distanceMeters = distanceMeters,
                    startOdometer = state.inputStartOdometer.toIntOrNull(),
                    endOdometer = state.inputEndOdometer.toIntOrNull(),
                    memo = "",
                )
            if (values.id == 0L) {
                insertTripRecordUseCase(values)
            } else {
                updateTripRecordUseCase(values)
            }
            _uiState.update { it.copy(editingRecordId = null, isInputDialogOpen = false) }
        }
    }

    private fun delete() {
        val state = _uiState.value
        val record = state.records.firstOrNull { it.id == state.deletingRecordId } ?: return
        if (record.isRecording) return
        viewModelScope.launch {
            deleteTripRecordUseCase(record)
            _uiState.update { it.copy(deletingRecordId = null) }
        }
    }
}
