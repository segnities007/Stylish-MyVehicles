package com.segnities007.stylish_myvehicles.presentation.screen.vehiclepager.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.segnities007.stylish_myvehicles.R
import com.segnities007.stylish_myvehicles.domain.model.Vehicle
import com.segnities007.stylish_myvehicles.domain.service.DeadlineResolver
import com.segnities007.stylish_myvehicles.presentation.theme.StylishMyVehiclesTheme

/**
 * 車検・自賠責・任意保険・次回メンテナンスの中で
 * 最も期限が近い項目を強調表示するアラートカード。
 */
@Composable
fun UrgentAlertCard(
    vehicle: Vehicle,
    modifier: Modifier = Modifier,
) {
    val candidates = buildList {
        DeadlineResolver.resolve(vehicle)
            ?.let { add(it.label to it.daysRemaining) }
    }
    val (label, days) = candidates.minByOrNull { it.second } ?: return

    val urgent = days <= 30
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = if (urgent) MaterialTheme.colorScheme.errorContainer
        else MaterialTheme.colorScheme.surfaceContainerHigh,
        contentColor = if (urgent) MaterialTheme.colorScheme.onErrorContainer
        else MaterialTheme.colorScheme.onSurface,
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Default.Event, contentDescription = null)
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    if (days < 0) stringResource(R.string.deadline_expired_alert, label)
                    else stringResource(R.string.deadline_days_alert, label, days),
                    style = MaterialTheme.typography.titleMedium,
                )
                if (days >= 0) {
                    Text(
                        stringResource(R.string.next_action_needed),
                        style = MaterialTheme.typography.bodySmall,
                        color = if (urgent) MaterialTheme.colorScheme.onErrorContainer
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Preview(name = "Urgent alert (soon)", showBackground = true, widthDp = 393)
@Composable
private fun UrgentAlertCardPreview() {
    StylishMyVehiclesTheme {
        Surface(Modifier.padding(20.dp)) {
            UrgentAlertCard(
                vehicle = Vehicle(
                    maker = "トヨタ", name = "プリウス",
                    firstRegistrationDate = java.time.LocalDate.now()
                        .minusYears(3)
                        .plusDays(20),
                ),
            )
        }
    }
}

@Preview(name = "Urgent alert (calm)", showBackground = true, widthDp = 393)
@Composable
private fun UrgentAlertCardCalmPreview() {
    StylishMyVehiclesTheme {
        Surface(Modifier.padding(20.dp)) {
            UrgentAlertCard(
                vehicle = Vehicle(
                    maker = "トヨタ", name = "プリウス",
                    firstRegistrationDate = java.time.LocalDate.now()
                        .minusYears(1),
                ),
            )
        }
    }
}
