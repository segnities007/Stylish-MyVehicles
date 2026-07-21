package com.segnities007.stylish_myvehicles.presentation.screen.vehicledetail.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.segnities007.stylish_myvehicles.domain.model.MaintenanceCategory
import com.segnities007.stylish_myvehicles.domain.model.MaintenanceSchedule
import com.segnities007.stylish_myvehicles.presentation.components.molecules.StylishConnectedListItemColumn
import com.segnities007.stylish_myvehicles.presentation.components.molecules.models.StylishConnectedListItem
import com.segnities007.stylish_myvehicles.presentation.theme.StylishMyVehiclesTheme
import java.time.LocalDate
import java.time.temporal.ChronoUnit

/** メンテナンス目安の一覧。タップで編集ダイアログへ誘導する。 */
@Composable
fun MaintenanceScheduleSection(
    schedules: List<MaintenanceSchedule>,
    onEdit: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val today = LocalDate.now()
    StylishConnectedListItemColumn(
        modifier = modifier,
        items = schedules.map { schedule ->
            StylishConnectedListItem(
                headline = schedule.category.label,
                supportingText = buildScheduleStatus(schedule, today),
                onClick = { onEdit(schedule.id) },
                trailingContent = {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward, null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                },
            )
        },
    )
}

private fun buildScheduleStatus(schedule: MaintenanceSchedule, today: LocalDate): String {
    val parts = mutableListOf<String>()

    schedule.intervalMonths?.let { interval ->
        schedule.lastDoneDate?.let { lastDone ->
            val nextDue = lastDone.plusMonths(interval.toLong())
            val days = ChronoUnit.DAYS.between(today, nextDue)
            parts.add(
                when {
                    days < 0 -> "⚠️ 期間超過"
                    days <= 30 -> "⚠️ あと${days}日"
                    else -> "あと${days}日（${interval}ヶ月ごと）"
                }
            )
        } ?: parts.add("${interval}ヶ月ごと（未実施）")
    }

    schedule.intervalKm?.let { interval ->
        schedule.lastDoneOdometer?.let { lastOdo ->
            parts.add("${String.format("%,d", lastOdo + interval)}kmで次回")
        } ?: parts.add("${String.format("%,d", interval)}kmごと（未実施）")
    }

    return parts.joinToString(" / ")
        .ifEmpty { "未設定" }
}

@Preview(name = "Maintenance schedule section", showBackground = true, widthDp = 393)
@Composable
private fun MaintenanceScheduleSectionPreview() {
    StylishMyVehiclesTheme {
        Surface(Modifier.padding(20.dp)) {
            MaintenanceScheduleSection(
                schedules = listOf(
                    MaintenanceSchedule(
                        id = 1, vehicleId = 1, category = MaintenanceCategory.OIL,
                        intervalKm = 5000, intervalMonths = 6,
                        lastDoneDate = LocalDate.now()
                            .minusMonths(5),
                        lastDoneOdometer = 40000,
                    ),
                    MaintenanceSchedule(
                        id = 2, vehicleId = 1, category = MaintenanceCategory.TIRE,
                        intervalKm = 30000,
                    ),
                ),
                onEdit = {},
            )
        }
    }
}
