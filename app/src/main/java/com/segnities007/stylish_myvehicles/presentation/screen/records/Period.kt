package com.segnities007.stylish_myvehicles.presentation.screen.records

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

/**
 * 記録画面のページャー粒度。月/年/週/すべてを切り替えられる。
 */
enum class PeriodMode(val label: String) {
    MONTHLY("月単位"),
    YEARLY("年単位"),
    WEEKLY("週単位"),
    ALL("すべて"),
}

/**
 * ページャーの1ページを表す期間。[start, endInclusive] の日付範囲を持つ。
 */
data class Period(
    val label: String,
    val start: LocalDate,
    val endInclusive: LocalDate,
)

/**
 * グラフの横軸などに使う、期間内のサブ区間。
 */
data class SubPeriod(
    val label: String,
    val start: LocalDate,
    val endInclusive: LocalDate,
)

/**
 * ある期間を、粒度に応じたサブ区間に分割する。
 * - 月単位: その月で終わる直近6か月
 * - 年単位: その年の12か月
 * - 週単位: その週の7日
 * - すべて: 今月で終わる直近6か月
 */
fun subPeriods(period: Period, mode: PeriodMode): List<SubPeriod> = when (mode) {
    PeriodMode.MONTHLY -> {
        val ym = YearMonth.from(period.start)
        (5 downTo 0).map { ago ->
            val m = ym.minusMonths(ago.toLong())
            SubPeriod("${m.monthValue}月", m.atDay(1), m.atEndOfMonth())
        }
    }

    PeriodMode.ALL -> {
        val ym = YearMonth.now()
        (5 downTo 0).map { ago ->
            val m = ym.minusMonths(ago.toLong())
            SubPeriod("${m.monthValue}月", m.atDay(1), m.atEndOfMonth())
        }
    }

    PeriodMode.YEARLY -> {
        val year = period.start.year
        (1..12).map { mo ->
            val m = YearMonth.of(year, mo)
            SubPeriod("${mo}月", m.atDay(1), m.atEndOfMonth())
        }
    }

    PeriodMode.WEEKLY -> {
        (0..6).map { d ->
            val day = period.start.plusDays(d.toLong())
            SubPeriod("${day.dayOfMonth}日", day, day)
        }
    }
}

/**
 * 粒度と記録日付から、連続した期間リスト（古い→新しい）を生成する。
 * 記録が無い場合でも現在＋1つ前の期間を最低限用意し、スワイプできるようにする。
 */
fun computePeriods(
    mode: PeriodMode,
    recordDates: List<LocalDate>,
    now: LocalDate,
): List<Period> = when (mode) {
    PeriodMode.MONTHLY -> {
        val recordMonths = recordDates.map { YearMonth.from(it) }
        continuousMonths(recordMonths, YearMonth.from(now)).map { m ->
            Period(
                label = m.format(DateTimeFormatter.ofPattern("yyyy年M月")),
                start = m.atDay(1),
                endInclusive = m.atEndOfMonth(),
            )
        }
    }

    PeriodMode.YEARLY -> {
        val nowYear = now.year
        val oldestYear = recordDates.minOfOrNull { it.year } ?: nowYear
        val boundedOldest = minOf(oldestYear, nowYear - 1)
        (boundedOldest..nowYear).map { y ->
            Period(
                label = "${y}年",
                start = LocalDate.of(y, 1, 1),
                endInclusive = LocalDate.of(y, 12, 31),
            )
        }
    }

    PeriodMode.WEEKLY -> {
        val currentMonday = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val oldestMonday = (recordDates.minOrNull() ?: now)
            .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val boundedOldest = if (oldestMonday.isAfter(currentMonday.minusWeeks(1))) {
            currentMonday.minusWeeks(1)
        } else {
            oldestMonday
        }
        buildList {
            var monday = boundedOldest
            while (!monday.isAfter(currentMonday)) {
                val sunday = monday.plusDays(6)
                add(
                    Period(
                        label = "${monday.monthValue}/${monday.dayOfMonth}〜${sunday.monthValue}/${sunday.dayOfMonth}",
                        start = monday,
                        endInclusive = sunday,
                    )
                )
                monday = monday.plusWeeks(1)
            }
        }
    }

    PeriodMode.ALL -> {
        val oldest = recordDates.minOrNull() ?: now
        listOf(
            Period(
                label = "すべての期間",
                start = oldest,
                endInclusive = now,
            )
        )
    }
}
