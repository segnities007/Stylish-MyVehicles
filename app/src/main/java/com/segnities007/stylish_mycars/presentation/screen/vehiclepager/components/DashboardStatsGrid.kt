package com.segnities007.stylish_mycars.presentation.screen.vehiclepager.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.segnities007.stylish_mycars.presentation.components.atoms.utils.stylishConnectedGridCorners
import com.segnities007.stylish_mycars.presentation.components.atoms.utils.stylishConnectedShape
import com.segnities007.stylish_mycars.presentation.screen.vehiclepager.VehicleDashboard
import com.segnities007.stylish_mycars.presentation.theme.StylishMyCarsTheme

private data class StatCell(
    val icon: ImageVector,
    val label: String,
    val value: String,
    val onClick: () -> Unit,
)

/**
 * 費用・燃費のサマリーを2x2の接続グリッドで表示。
 * 各セルはアイコン付きで内容をイメージしやすくし、
 * 同一行のセルは最も高いセルに高さを揃える。
 */
@Composable
fun DashboardStatsGrid(
    dashboard: VehicleDashboard,
    onCostClick: () -> Unit,
    onFuelClick: () -> Unit,
    modifier: Modifier = Modifier,
    showFuelEconomy: Boolean = true,
) {
    val stats = buildList {
        add(
            StatCell(
                Icons.Default.AccountBalanceWallet, "今月の費用",
                "${String.format("%,d", dashboard.monthlyCost)}円", onCostClick,
            ),
        )
        add(
            StatCell(
                Icons.Default.CalendarMonth, "年間費用",
                "${String.format("%,d", dashboard.yearlyCost)}円", onCostClick,
            ),
        )
        add(
            StatCell(
                Icons.Default.Payments, "総費用",
                "${String.format("%,d", dashboard.totalCost)}円", onCostClick,
            ),
        )
        add(
            StatCell(
                Icons.Default.TrendingUp, "月平均",
                dashboard.averageMonthlyCost?.let { "${String.format("%,d", it.toInt())}円" } ?: "--",
                onCostClick,
            ),
        )
        if (showFuelEconomy) {
            add(
                StatCell(
                    Icons.Default.LocalGasStation, "平均燃費",
                    dashboard.averageFuelEconomy?.let { "%.1f km/L".format(it) } ?: "--", onFuelClick,
                ),
            )
        }
    }
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        stats.chunked(2).forEachIndexed { rowIndex, rowStats ->
            Row(
                modifier = Modifier.height(IntrinsicSize.Max),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                rowStats.forEachIndexed { colIndex, stat ->
                    val index = rowIndex * 2 + colIndex
                    Surface(
                        modifier = Modifier.weight(1f).fillMaxHeight()
                            .clickable(onClick = stat.onClick),
                        shape = stylishConnectedShape(stylishConnectedGridCorners(index, stats.size, 2)),
                        color = MaterialTheme.colorScheme.surfaceContainerHighest,
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    stat.icon,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp),
                                )
                                Spacer(Modifier.padding(start = 6.dp))
                                Text(
                                    stat.label,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                            Spacer(Modifier.height(6.dp))
                            Text(stat.value, style = MaterialTheme.typography.titleLarge)
                        }
                    }
                }
                repeat(2 - rowStats.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}

@Preview(name = "Dashboard stats grid", showBackground = true, widthDp = 393)
@Composable
private fun DashboardStatsGridPreview() {
    StylishMyCarsTheme {
        Surface(Modifier.padding(20.dp)) {
            DashboardStatsGrid(
                dashboard = VehicleDashboard(
                    monthlyCost = 12340,
                    yearlyCost = 145000,
                    totalCost = 523000,
                    averageFuelEconomy = 18.5,
                ),
                onCostClick = {},
                onFuelClick = {},
            )
        }
    }
}

@Preview(name = "Dashboard stats grid (no data)", showBackground = true, widthDp = 393)
@Composable
private fun DashboardStatsGridEmptyPreview() {
    StylishMyCarsTheme {
        Surface(Modifier.padding(20.dp)) {
            DashboardStatsGrid(
                dashboard = VehicleDashboard(),
                onCostClick = {},
                onFuelClick = {},
            )
        }
    }
}
