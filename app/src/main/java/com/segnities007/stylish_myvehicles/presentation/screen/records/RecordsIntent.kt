package com.segnities007.stylish_myvehicles.presentation.screen.records

sealed interface RecordsIntent {
    data class PageChanged(val page: Int) : RecordsIntent
    data object NavigateBack : RecordsIntent
    data class NavigateToFuel(val recordId: Long?) : RecordsIntent
    data class NavigateToMaintenance(val recordId: Long?) : RecordsIntent
    data class NavigateToCost(val recordId: Long?) : RecordsIntent
}
