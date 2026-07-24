package com.segnities007.stylish_myvehicles.presentation.screen.recordslist

sealed interface RecordsListIntent {
    data object NavigateBack : RecordsListIntent
    data class OpenRecord(val entry: RecordEntry) : RecordsListIntent
}
