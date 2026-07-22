package com.segnities007.stylish_myvehicles.presentation.screen.records

/**
 * 記録画面のトピック。関心の分離のため、各トピック画面はそのトピックの情報だけを表示する。
 */
enum class RecordTopic(val label: String) {
    FUEL("給油"),
    MAINTENANCE("整備"),
    COST("費用"),
}
