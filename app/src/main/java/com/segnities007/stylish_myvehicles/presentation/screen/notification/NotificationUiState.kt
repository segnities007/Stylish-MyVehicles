package com.segnities007.stylish_myvehicles.presentation.screen.notification

import com.segnities007.stylish_myvehicles.domain.service.DeadlineInfo

data class NotificationUiState(
    val isLoading: Boolean = true,
    val deadlines: List<VehicleDeadlineItem> = emptyList(),
)

data class VehicleDeadlineItem(
    val vehicleId: Long,
    val vehicleName: String,
    val deadline: DeadlineInfo,
)
