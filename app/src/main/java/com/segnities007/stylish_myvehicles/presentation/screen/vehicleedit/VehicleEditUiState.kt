package com.segnities007.stylish_myvehicles.presentation.screen.vehicleedit

import androidx.compose.runtime.Immutable
import com.segnities007.stylish_myvehicles.domain.model.VehicleCategory
import java.time.LocalDate

@Immutable
data class VehicleEditUiState(
    val vehicleId: Long? = null,
    // 種別
    val category: VehicleCategory = VehicleCategory.CAR,
    // 基本情報
    val maker: String = "",
    val name: String = "",
    val grade: String = "",
    val year: String = "",
    val modelCode: String = "",
    val plateNumber: String = "",
    val displacement: String = "",
    val weight: String = "",
    val maxLoadKg: String = "",
    val color: String = "",
    // 期限・保険
    val firstRegistrationDate: LocalDate? = null,
    val inspectionExpiry: LocalDate? = null,
    val jibaiExpiry: LocalDate? = null,
    val insuranceExpiry: LocalDate? = null,
    val insuranceCompany: String = "",
    val insuranceRank: String = "",
    // 状態
    val isEditing: Boolean = false,
    val isSaving: Boolean = false,
    val showDeleteDialog: Boolean = false,
) {
    val canSave: Boolean
        get() = maker.isNotBlank() &&
            name.isNotBlank() &&
            yearError == null &&
            displacementError == null &&
            weightError == null &&
            (category != VehicleCategory.TRUCK || maxLoadKgError == null) &&
            insuranceRankError == null &&
            !isSaving

    val makerError: String?
        get() = if (maker.isBlank()) "メーカーは必須です" else null

    val nameError: String?
        get() = if (name.isBlank()) "車種名は必須です" else null

    val yearError: String?
        get() = validateOptionalInt(year) {
            if (it !in 1900..2100) "1900〜2100の範囲で入力してください" else null
        }

    val displacementError: String?
        get() = validateOptionalInt(displacement, ::positiveValueError)

    val weightError: String?
        get() = validateOptionalInt(weight, ::positiveValueError)

    val maxLoadKgError: String?
        get() = validateOptionalInt(maxLoadKg, ::positiveValueError)

    val insuranceRankError: String?
        get() = validateOptionalInt(insuranceRank) {
            if (it !in 1..20) "1〜20の範囲で入力してください" else null
        }
}

private fun validateOptionalInt(
    input: String,
    validate: (Int) -> String?,
): String? {
    if (input.isBlank()) return null
    val value = input.toIntOrNull() ?: return "数値で入力してください"
    return validate(value)
}

private fun positiveValueError(value: Int): String? =
    if (value <= 0) "0より大きい値を入力してください" else null
