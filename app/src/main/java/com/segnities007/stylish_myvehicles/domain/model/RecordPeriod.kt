package com.segnities007.stylish_myvehicles.domain.model

import java.time.LocalDate

/** レコード一覧の表示期間フィルタ。 */
enum class RecordPeriod(val label: String) {
    MONTH_1("1ヶ月"),
    MONTH_3("3ヶ月"),
    MONTH_6("6ヶ月"),
    YEAR_1("1年"),
    ALL("すべて"),
    ;

    /** 期間の開始日（同日を含む）。ALL は下限なし（null）。 */
    fun startDate(today: LocalDate): LocalDate? = when (this) {
        MONTH_1 -> today.minusMonths(1)
        MONTH_3 -> today.minusMonths(3)
        MONTH_6 -> today.minusMonths(6)
        YEAR_1 -> today.minusYears(1)
        ALL -> null
    }

    /** 期間内のレコードのみを絞り込む。ALL はすべて返す。 */
    fun <T> filter(
        items: List<T>,
        today: LocalDate = LocalDate.now(),
        dateOf: (T) -> LocalDate,
    ): List<T> {
        val start = startDate(today) ?: return items
        return items.filter { !dateOf(it).isBefore(start) }
    }
}
