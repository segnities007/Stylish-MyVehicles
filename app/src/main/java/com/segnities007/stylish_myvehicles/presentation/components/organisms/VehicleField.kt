package com.segnities007.stylish_myvehicles.presentation.components.organisms

/**
 * 車両情報の編集対象フィールド。ダイアログの入力方式は [inputType] で決まる。
 */
enum class VehicleFieldInputType {
    TEXT,
    NUMBER,
    DATE,
    SELECTION,
}

/**
 * 車両情報・期限管理で表示/編集できる全フィールド。
 */
enum class VehicleField(
    val label: String,
    val inputType: VehicleFieldInputType,
    val unit: String? = null,
) {
    CATEGORY("車両カテゴリ", VehicleFieldInputType.SELECTION),
    MAKER("メーカー", VehicleFieldInputType.TEXT),
    NAME("車両名", VehicleFieldInputType.TEXT),
    GRADE("グレード", VehicleFieldInputType.TEXT),
    YEAR("年式", VehicleFieldInputType.NUMBER, "年"),
    MODEL_CODE("型式", VehicleFieldInputType.TEXT),
    PLATE_NUMBER("ナンバー", VehicleFieldInputType.TEXT),
    VIN("車台番号", VehicleFieldInputType.TEXT),
    DISPLACEMENT("排気量", VehicleFieldInputType.NUMBER, "cc"),
    WEIGHT("車両重量", VehicleFieldInputType.NUMBER, "kg"),
    MAX_LOAD("最大積載量", VehicleFieldInputType.NUMBER, "kg"),
    COLOR("カラー", VehicleFieldInputType.TEXT),
    FIRST_REGISTRATION_DATE("初度登録日", VehicleFieldInputType.DATE),
    INSPECTION_EXPIRY("車検満了日", VehicleFieldInputType.DATE),
    JIBAI_EXPIRY("自賠責満了日", VehicleFieldInputType.DATE),
    INSURANCE_EXPIRY("任意保険満了日", VehicleFieldInputType.DATE),
    INSURANCE_COMPANY("保険会社", VehicleFieldInputType.TEXT),
    INSURANCE_RANK("保険等級", VehicleFieldInputType.NUMBER, "等級"),
    MEMO("メモ", VehicleFieldInputType.TEXT),
}
