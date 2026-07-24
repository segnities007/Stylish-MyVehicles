package com.segnities007.stylish_myvehicles.domain.model

enum class MaintenanceCategory(val label: String) {
    OIL("エンジンオイル"),
    OIL_FILTER("オイルフィルター"),
    TIRE("タイヤ"),
    BATTERY("バッテリー"),
    BRAKE("ブレーキ"),
    WIPER("ワイパー"),
    AIR_FILTER("エアコンフィルター"),
    COOLANT("冷却水"),
    SPARK_PLUG("スパークプラグ"),
    BELT("ベルト類"),
    CHAIN("チェーン"),
    GEAR("変速機"),
    GENERAL_INSPECTION("全体点検"),
    INSPECTION_12("法定12ヶ月点検"),
    INSPECTION_24("法定24ヶ月点検"),
    SHAKEN("車検"),
    OTHER("その他"),
    ;

    companion object {
        /** 乗り物カテゴリに関連する整備カテゴリ（入力候補）。末尾は常に OTHER。 */
        fun relevantFor(category: VehicleCategory): List<MaintenanceCategory> = when (category) {
            VehicleCategory.CAR, VehicleCategory.KEI_CAR -> listOf(
                OIL, OIL_FILTER, TIRE, BATTERY, BRAKE, WIPER, AIR_FILTER, COOLANT,
                SPARK_PLUG, BELT, INSPECTION_12, INSPECTION_24, SHAKEN, OTHER,
            )

            VehicleCategory.MOTORCYCLE -> listOf(
                OIL, OIL_FILTER, CHAIN, TIRE, BATTERY, BRAKE, SPARK_PLUG, COOLANT,
                INSPECTION_12, INSPECTION_24, SHAKEN, OTHER,
            )

            VehicleCategory.BICYCLE -> listOf(
                TIRE, BRAKE, CHAIN, GEAR, GENERAL_INSPECTION, OTHER,
            )

            VehicleCategory.TRUCK -> listOf(
                OIL, OIL_FILTER, TIRE, BATTERY, BRAKE, COOLANT, BELT,
                INSPECTION_12, INSPECTION_24, SHAKEN, OTHER,
            )

            VehicleCategory.OTHER -> entries.toList()
        }
    }
}
