package com.segnities007.stylish_myvehicles.presentation.screen.vehicledetail

import androidx.compose.runtime.Immutable
import com.segnities007.stylish_myvehicles.domain.model.Vehicle
import com.segnities007.stylish_myvehicles.domain.model.VehicleCategory
import com.segnities007.stylish_myvehicles.presentation.components.organisms.VehicleField
import java.time.LocalDate

@Immutable
data class VehicleDetailUiState(
    val vehicle: Vehicle? = null,
    val taxPaidThisYear: Boolean = false,
    val isLoading: Boolean = true,
    // フィールド編集ダイアログ
    val editingField: VehicleField? = null,
    val fieldInputText: String = "",
    val fieldInputDate: LocalDate? = null,
    val fieldInputCategory: VehicleCategory = VehicleCategory.CAR,
)
