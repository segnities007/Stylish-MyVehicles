package com.segnities007.stylish_myvehicles.presentation.screen.vehicleedit

import com.segnities007.stylish_myvehicles.domain.model.VehicleCategory
import java.time.LocalDate

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
    val jibaiExpiry: LocalDate? = null,
    val insuranceExpiry: LocalDate? = null,
    val insuranceCompany: String = "",
    val insuranceRank: String = "",
    val taxPaid: Boolean = false,
    // 状態
    val isEditing: Boolean = false,
    val isSaving: Boolean = false,
    val showDeleteDialog: Boolean = false,
) {
    val canSave: Boolean
        get() = maker.isNotBlank() && name.isNotBlank() && !isSaving

    val makerError: String?
        get() = if (maker.isBlank()) "メーカーは必須です" else null

    val nameError: String?
        get() = if (name.isBlank()) "車種名は必須です" else null

    val yearError: String?
        get() = year.toIntOrNull()
            ?.let {
                if (it < 1900 || it > 2100) "1900〜2100の範囲で入力してください" else null
            } ?: year.takeIf { it.isNotBlank() }
            ?.let { "数値で入力してください" }

    val displacementError: String?
        get() = displacement.toIntOrNull()
            ?.let {
                if (it <= 0) "0より大きい値を入力してください" else null
            } ?: displacement.takeIf { it.isNotBlank() }
            ?.let { "数値で入力してください" }

    val weightError: String?
        get() = weight.toIntOrNull()
            ?.let {
                if (it <= 0) "0より大きい値を入力してください" else null
            } ?: weight.takeIf { it.isNotBlank() }
            ?.let { "数値で入力してください" }

    val maxLoadKgError: String?
        get() = maxLoadKg.toIntOrNull()
            ?.let {
                if (it <= 0) "0より大きい値を入力してください" else null
            } ?: maxLoadKg.takeIf { it.isNotBlank() }
            ?.let { "数値で入力してください" }
}
