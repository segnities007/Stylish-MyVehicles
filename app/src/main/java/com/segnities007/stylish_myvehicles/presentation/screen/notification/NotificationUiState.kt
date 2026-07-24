package com.segnities007.stylish_myvehicles.presentation.screen.notification

import androidx.compose.runtime.Immutable
import com.segnities007.stylish_myvehicles.domain.service.DeadlineInfo

@Immutable
data class NotificationUiState(
    val isLoading: Boolean = true,
    val deadlines: List<VehicleDeadlineItem> = emptyList(),
)

@Immutable
data class VehicleDeadlineItem(
    val vehicleId: Long,
    val vehicleName: String,
    val deadline: DeadlineInfo,
)
