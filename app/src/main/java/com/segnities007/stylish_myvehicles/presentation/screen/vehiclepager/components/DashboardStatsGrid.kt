package com.segnities007.stylish_myvehicles.presentation.screen.vehiclepager.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.segnities007.stylish_myvehicles.R
import com.segnities007.stylishui.components.molecules.StylishConnectedCardGrid
import com.segnities007.stylishui.components.models.StylishConnectedCardItem
import com.segnities007.stylish_myvehicles.presentation.screen.vehiclepager.VehicleDashboard
import com.segnities007.stylish_myvehicles.presentation.theme.StylishMyVehiclesTheme

/**
 * 費用・燃費のサマリーを接続グリッドで表示。
 */
@Composable
fun DashboardStatsGrid(
    dashboard: VehicleDashboard,
    modifier: Modifier = Modifier,
    showFuelEconomy: Boolean = true,
) {
    StylishConnectedCardGrid(
        items = buildList {
            add(
                StylishConnectedCardItem(
                    title = "${String.format("%,d", dashboard.monthlyCost)}円",
                    supportingText = stringResource(R.string.this_month_cost_label),
                    trailingContent = {
                        Icon(
                            Icons.Default.AccountBalanceWallet,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp),
                        )
                    },
                ),
            )
            add(
                StylishConnectedCardItem(
                    title = "${String.format("%,d", dashboard.yearlyCost)}円",
                    supportingText = stringResource(R.string.yearly_cost_label),
                    trailingContent = {
                        Icon(
                            Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp),
                        )
                    },
                ),
            )
            add(
                StylishConnectedCardItem(
                    title = "${String.format("%,d", dashboard.totalCost)}円",
                    supportingText = stringResource(R.string.total_cost_label),
                    trailingContent = {
                        Icon(
                            Icons.Default.Payments,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp),
                        )
                    },
                ),
            )
            add(
                StylishConnectedCardItem(
                    title = dashboard.averageMonthlyCost?.let {
                        "${
                            String.format(
                                "%,d",
                                it.toInt()
                            )
                        }円"
                    } ?: "--",
                    supportingText = stringResource(R.string.monthly_average_label),
                    trailingContent = {
                        Icon(
                            Icons.AutoMirrored.Filled.TrendingUp,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp),
                        )
                    },
                ),
            )
            if (showFuelEconomy) {
                add(
                    StylishConnectedCardItem(
                        title = dashboard.averageFuelEconomy?.let { "%.1f km/L".format(it) }
                            ?: "--",
                        supportingText = stringResource(R.string.average_fuel_economy_label),
                        trailingContent = {
                            Icon(
                                Icons.Default.LocalGasStation,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp),
                            )
                        },
                    ),
                )
            }
            if (dashboard.totalDistance > 0) {
                add(
                    StylishConnectedCardItem(
                        title = "${String.format("%,d", dashboard.totalDistance)}km",
                        supportingText = stringResource(R.string.total_distance_label),
                        trailingContent = {
                            Icon(
                                Icons.Default.Speed,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp),
                            )
                        },
                    ),
                )
            }
            dashboard.costPerKm?.let { cpk ->
                add(
                    StylishConnectedCardItem(
                        title = stringResource(R.string.cost_per_km_format, cpk),
                        supportingText = stringResource(R.string.cost_per_km_label),
                        trailingContent = {
                            Icon(
                                Icons.AutoMirrored.Filled.TrendingUp,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp),
                            )
                        },
                    ),
                )
            }
        },
        columns = 2,
        modifier = modifier,
    )
}

@Preview(name = "Dashboard stats grid", showBackground = true, widthDp = 393)
@Composable
private fun DashboardStatsGridPreview() {
    StylishMyVehiclesTheme {
        Surface(Modifier.padding(20.dp)) {
            DashboardStatsGrid(
                dashboard = VehicleDashboard(
                    monthlyCost = 12340,
                    yearlyCost = 145000,
                    totalCost = 523000,
                    averageFuelEconomy = 18.5,
                ),
            )
        }
    }
}

@Preview(name = "Dashboard stats grid (no data)", showBackground = true, widthDp = 393)
@Composable
private fun DashboardStatsGridEmptyPreview() {
    StylishMyVehiclesTheme {
        Surface(Modifier.padding(20.dp)) {
            DashboardStatsGrid(
                dashboard = VehicleDashboard(),
            )
        }
    }
}
