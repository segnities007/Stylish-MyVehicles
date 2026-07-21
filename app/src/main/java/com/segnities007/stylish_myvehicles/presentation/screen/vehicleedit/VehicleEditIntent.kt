package com.segnities007.stylish_myvehicles.presentation.screen.vehicleedit

import com.segnities007.stylish_myvehicles.domain.model.VehicleCategory
import java.time.LocalDate

sealed interface VehicleEditIntent {
    // 種別
    data class CategoryChanged(val value: VehicleCategory) : VehicleEditIntent

    // 基本情報
    data class MakerChanged(val value: String) : VehicleEditIntent
    data class NameChanged(val value: String) : VehicleEditIntent
    data class GradeChanged(val value: String) : VehicleEditIntent
    data class YearChanged(val value: String) : VehicleEditIntent
    data class ModelCodeChanged(val value: String) : VehicleEditIntent
    data class PlateNumberChanged(val value: String) : VehicleEditIntent
    data class DisplacementChanged(val value: String) : VehicleEditIntent
    data class WeightChanged(val value: String) : VehicleEditIntent
    data class MaxLoadKgChanged(val value: String) : VehicleEditIntent
    data class ColorChanged(val value: String) : VehicleEditIntent

    // 期限・保険
    data class FirstRegistrationDateChanged(val value: LocalDate?) : VehicleEditIntent
    data class JibaiExpiryChanged(val value: LocalDate?) : VehicleEditIntent
    data class InsuranceExpiryChanged(val value: LocalDate?) : VehicleEditIntent
    data class InsuranceCompanyChanged(val value: String) : VehicleEditIntent
    data class InsuranceRankChanged(val value: String) : VehicleEditIntent

    // アクション
    data object Save : VehicleEditIntent
    data object RequestDelete : VehicleEditIntent
    data object ConfirmDelete : VehicleEditIntent
    data object DismissDeleteDialog : VehicleEditIntent
    data object NavigateBack : VehicleEditIntent
}
