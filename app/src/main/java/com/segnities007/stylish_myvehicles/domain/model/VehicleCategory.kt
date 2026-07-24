package com.segnities007.stylish_myvehicles.domain.model

/**
 * 管理対象の乗り物カテゴリ。
 * カテゴリごとに税額・車検要否・メンテナンス初期値などのドメインルールが異なる。
 *
 * @param usesFuel 燃料（給油）管理の対象か。自転車は false。
 * @param requiresInsurance 自賠責保険の対象か。自転車は false。
 */
enum class VehicleCategory(
    val label: String,
    val usesFuel: Boolean = true,
    val requiresInsurance: Boolean = true,
) {
    CAR("乗用車"),
    KEI_CAR("軽自動車"),
    MOTORCYCLE("バイク"),
    BICYCLE("自転車", usesFuel = false, requiresInsurance = false),
    TRUCK("トラック"),
    OTHER("その他"),
}
