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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.segnities007.stylish_myvehicles.domain.model.Vehicle
import com.segnities007.stylish_myvehicles.domain.service.DeadlineResolver
import com.segnities007.stylish_myvehicles.presentation.screen.vehiclepager.VehicleDashboard
import com.segnities007.stylish_myvehicles.presentation.theme.StylishMyVehiclesTheme

/**
 * 車検・自賠責・任意保険・次回メンテナンスの中で
 * 最も期限が近い項目を強調表示するアラートカード。
 */
@Composable
fun UrgentAlertCard(
    vehicle: Vehicle,
    dashboard: VehicleDashboard,
    modifier: Modifier = Modifier,
) {
    val candidates = buildList {
        DeadlineResolver.resolve(vehicle)
            ?.let { add(it.label to it.daysRemaining) }
        dashboard.nextMaintenanceLabel?.let { label ->
            dashboard.nextMaintenanceDays?.let { days -> add(label to days) }
        }
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
                    if (days < 0) "⚠️ $label が期限切れです"
                    else "$label まであと${days}日",
                    style = MaterialTheme.typography.titleMedium,
                )
                if (days >= 0) {
                    Text(
                        "次の対応が必要な項目です",
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
                dashboard = VehicleDashboard(),
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
                dashboard = VehicleDashboard(),
            )
        }
    }
}
