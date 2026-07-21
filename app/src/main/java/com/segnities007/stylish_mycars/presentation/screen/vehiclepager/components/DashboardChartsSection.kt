package com.segnities007.stylish_mycars.presentation.screen.vehiclepager.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.segnities007.stylish_mycars.domain.model.CostCategory
import com.segnities007.stylish_mycars.presentation.components.charts.BarChartData
import com.segnities007.stylish_mycars.presentation.components.charts.LineChartData
import com.segnities007.stylish_mycars.presentation.components.charts.PieChartData
import com.segnities007.stylish_mycars.presentation.components.charts.SimpleBarChart
import com.segnities007.stylish_mycars.presentation.components.charts.SimpleLineChart
import com.segnities007.stylish_mycars.presentation.components.charts.SimplePieChart
import com.segnities007.stylish_mycars.presentation.components.charts.costCategoryColor
import com.segnities007.stylish_mycars.presentation.components.organisms.StylishSectionTitle
import com.segnities007.stylish_mycars.presentation.screen.vehiclepager.VehicleDashboard
import com.segnities007.stylish_mycars.presentation.theme.StylishMyCarsTheme

/** 燃費推移・費用カテゴリ・月次費用のグラフ群。データがある項目のみ表示。 */
@Composable
fun DashboardChartsSection(
    dashboard: VehicleDashboard,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        if (dashboard.fuelEconomyTrend.size >= 2) {
            StylishSectionTitle("燃費推移 (km/L)")
            SimpleLineChart(
                data = dashboard.fuelEconomyTrend.map { LineChartData(it.first, it.second) },
            )
            Spacer(Modifier.height(20.dp))
        }

        if (dashboard.costByCategory.size >= 2) {
            StylishSectionTitle("費用カテゴリ")
            val pieData = dashboard.costByCategory.map { (category, total) ->
                PieChartData(category.label, total.toFloat(), costCategoryColor(category.ordinal))
            }
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                SimplePieChart(data = pieData)
                Column {
                    pieData.forEach { slice ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                Modifier.padding(end = 6.dp).size(10.dp)
                                    .background(slice.color, CircleShape),
                            )
                            Text(
                                "${slice.label}: ${String.format("%,d", slice.value.toInt())}円",
                                style = MaterialTheme.typography.labelSmall,
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(20.dp))
        }

        if (dashboard.monthlyCostTrend.any { it.second > 0 }) {
            StylishSectionTitle("月次費用")
            SimpleBarChart(
                data = dashboard.monthlyCostTrend.map { BarChartData(it.first, it.second) },
            )
        }
    }
}

@Preview(name = "Dashboard charts", showBackground = true, widthDp = 393)
@Composable
private fun DashboardChartsSectionPreview() {
    StylishMyCarsTheme {
        Surface(Modifier.padding(20.dp)) {
            DashboardChartsSection(
                dashboard = VehicleDashboard(
                    costByCategory = listOf(
                        CostCategory.FUEL to 120000,
                        CostCategory.MAINTENANCE to 80000,
                        CostCategory.INSURANCE to 60000,
                        CostCategory.TAX to 45000,
                    ),
                    monthlyCostTrend = listOf(
                        "2月" to 8000f, "3月" to 12000f, "4月" to 45000f,
                        "5月" to 9000f, "6月" to 15000f, "7月" to 11000f,
                    ),
                    fuelEconomyTrend = listOf(
                        "5/2" to 17.2f, "5/16" to 18.1f, "5/30" to 17.8f,
                        "6/13" to 18.9f, "6/27" to 18.4f, "7/11" to 19.0f,
                    ),
                ),
            )
        }
    }
}
