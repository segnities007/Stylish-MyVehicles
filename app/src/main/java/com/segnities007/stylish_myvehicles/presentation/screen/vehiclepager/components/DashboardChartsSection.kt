package com.segnities007.stylish_myvehicles.presentation.screen.vehiclepager.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.segnities007.stylish_myvehicles.domain.model.CostCategory
import com.segnities007.stylish_myvehicles.presentation.components.molecules.BarChartData
import com.segnities007.stylish_myvehicles.presentation.components.molecules.LineChartData
import com.segnities007.stylish_myvehicles.presentation.components.molecules.PieChartData
import com.segnities007.stylish_myvehicles.presentation.components.molecules.costCategoryColor
import com.segnities007.stylish_myvehicles.presentation.components.organisms.BarChartSection
import com.segnities007.stylish_myvehicles.presentation.components.organisms.LineChartSection
import com.segnities007.stylish_myvehicles.presentation.components.organisms.PieChartSection
import com.segnities007.stylish_myvehicles.presentation.screen.vehiclepager.VehicleDashboard
import com.segnities007.stylish_myvehicles.presentation.theme.StylishMyVehiclesTheme

/** 燃費推移・費用カテゴリ・月次費用のグラフ群。データがなくてもスケルトンを表示。 */
@Composable
fun DashboardChartsSection(
    dashboard: VehicleDashboard,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        LineChartSection(
            title = "燃費推移 (km/L)",
            data = dashboard.fuelEconomyTrend.map { LineChartData(it.first, it.second) },
        )
        Spacer(Modifier.height(20.dp))

        PieChartSection(
            title = "費用カテゴリ",
            data = dashboard.costByCategory.map { (category, total) ->
                PieChartData(category.label, total.toFloat(), costCategoryColor(category.ordinal))
            },
        )
        Spacer(Modifier.height(20.dp))

        BarChartSection(
            title = "月次費用",
            data = dashboard.monthlyCostTrend.map { BarChartData(it.first, it.second) },
        )
    }
}

@Preview(name = "Dashboard charts", showBackground = true, widthDp = 393)
@Composable
private fun DashboardChartsSectionPreview() {
    StylishMyVehiclesTheme {
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
