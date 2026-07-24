package com.segnities007.stylish_myvehicles.presentation.screen.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.segnities007.stylish_myvehicles.domain.model.MaintenanceSchedule
import com.segnities007.stylish_myvehicles.domain.model.Vehicle
import com.segnities007.stylish_myvehicles.domain.repository.MaintenanceScheduleRepository
import com.segnities007.stylish_myvehicles.domain.repository.VehicleRepository
import com.segnities007.stylish_myvehicles.domain.service.DeadlineInfo
import com.segnities007.stylish_myvehicles.domain.service.InspectionCalculator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class NotificationViewModel(
    private val vehicleRepository: VehicleRepository,
    private val maintenanceScheduleRepository: MaintenanceScheduleRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(NotificationUiState())
    val uiState: StateFlow<NotificationUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            vehicleRepository.getAll().collect { vehicles ->
                if (vehicles.isEmpty()) {
                    _uiState.update { it.copy(isLoading = false, deadlines = emptyList()) }
                    return@collect
                }
                val scheduleFlows = vehicles.map { v ->
                    maintenanceScheduleRepository.getByVehicleId(v.id)
                }
                combine(scheduleFlows) { scheduleLists ->
                    vehicles.zip(scheduleLists.toList())
                }.collect { pairs ->
                    val now = LocalDate.now()
                    val items = pairs.flatMap { (vehicle, schedules) ->
                        buildDeadlines(vehicle, schedules, now)
                    }.sortedBy { it.deadline.daysRemaining }
                    _uiState.update {
                        it.copy(isLoading = false, deadlines = items)
                    }
                }
            }
        }
    }

    private fun buildDeadlines(
        vehicle: Vehicle,
        schedules: List<MaintenanceSchedule>,
        now: LocalDate,
    ): List<VehicleDeadlineItem> {
        val items = mutableListOf<VehicleDeadlineItem>()

        InspectionCalculator.currentExpiryFor(vehicle)?.let { date ->
            items += VehicleDeadlineItem(
                vehicleId = vehicle.id,
                vehicleName = vehicle.name,
                deadline = DeadlineInfo("車検", date, ChronoUnit.DAYS.between(now, date)),
            )
        }
        vehicle.jibaiExpiry?.let { date ->
            items += VehicleDeadlineItem(
                vehicleId = vehicle.id,
                vehicleName = vehicle.name,
                deadline = DeadlineInfo("自賠責保険", date, ChronoUnit.DAYS.between(now, date)),
            )
        }
        vehicle.insuranceExpiry?.let { date ->
            items += VehicleDeadlineItem(
                vehicleId = vehicle.id,
                vehicleName = vehicle.name,
                deadline = DeadlineInfo("任意保険", date, ChronoUnit.DAYS.between(now, date)),
            )
        }

        schedules.forEach { schedule ->
            val days = schedule.daysUntilDue(now) ?: return@forEach
            items += VehicleDeadlineItem(
                vehicleId = vehicle.id,
                vehicleName = vehicle.name,
                deadline = DeadlineInfo(schedule.category.label, schedule.dueDate!!, days),
            )
        }

        return items
    }
}
