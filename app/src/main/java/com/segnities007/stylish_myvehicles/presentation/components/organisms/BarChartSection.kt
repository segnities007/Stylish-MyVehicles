package com.segnities007.stylish_myvehicles.presentation.components.organisms

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.segnities007.stylish_myvehicles.presentation.components.molecules.BarChartData
import com.segnities007.stylish_myvehicles.presentation.components.molecules.SimpleBarChart
import com.segnities007.stylish_myvehicles.presentation.theme.StylishMyVehiclesTheme

@Composable
fun BarChartSection(
    title: String,
    data: List<BarChartData>,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        StylishSectionTitle(title)
        SimpleBarChart(data = data)
    }
}

@Preview(name = "Bar chart section", showBackground = true, widthDp = 393)
@Composable
private fun BarChartSectionPreview() {
    StylishMyVehiclesTheme {
        Surface(Modifier.padding(20.dp)) {
            BarChartSection(
                title = "月次費用",
                data = listOf(
                    BarChartData("2月", 8000f),
                    BarChartData("3月", 12000f),
                    BarChartData("4月", 45000f),
                ),
            )
        }
    }
}
